package com.bigtreetc.recycling;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class BaseTestContainerTest {

  static final LocalStackContainer LOCALSTACK_CONTAINER =
      new LocalStackContainer(
              DockerImageName.parse("public.ecr.aws/localstack/localstack:latest")
                  .asCompatibleSubstituteFor("localstack/localstack:latest"))
          .withServices(
              Service.S3, Service.DYNAMODB, Service.SQS, Service.SES, Service.IAM, Service.STS);

  static final GenericContainer<?> ECS_LOCAL_CONTAINER =
      new GenericContainer<>(
              DockerImageName.parse(
                  "public.ecr.aws/ecs-local/amazon-ecs-local-container-endpoints:latest"))
          .withNetwork(createCredentialsNetwork())
          .withCreateContainerCmdModifier(cmd -> cmd.withIpv4Address("169.254.170.2"))
          .withEnv("AWS_ACCESS_KEY_ID", "dummy")
          .withEnv("AWS_SECRET_ACCESS_KEY", "dummy")
          .withEnv("AWS_REGION", "us-east-1");

  static {
    LOCALSTACK_CONTAINER.start();

    String iamServiceEndpoint = LOCALSTACK_CONTAINER.getEndpointOverride(Service.IAM).toString();
    String stsServiceEndpoint = LOCALSTACK_CONTAINER.getEndpointOverride(Service.STS).toString();
    ECS_LOCAL_CONTAINER.addEnv("IAM_CUSTOM_ENDPOINT", iamServiceEndpoint);
    ECS_LOCAL_CONTAINER.addEnv("STS_CUSTOM_ENDPOINT", stsServiceEndpoint);
    ECS_LOCAL_CONTAINER.start();
  }

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("application.aws.s3.endpoint", getLocalStackEndpoint(Service.S3));
    // registry.add("application.aws.ecs.endpoint", getLocalStackEndpoint());
    registry.add("application.aws.dynamodb.endpoint", getLocalStackEndpoint(Service.DYNAMODB));
    registry.add("application.aws.sqs.endpoint", getLocalStackEndpoint(Service.SQS));
    registry.add("application.aws.ses.endpoint", getLocalStackEndpoint(Service.SES));
  }

  @NotNull
  private static Supplier<Object> getLocalStackEndpoint(Service service) {
    return () -> LOCALSTACK_CONTAINER.getEndpointOverride(service);
  }

  private static Network createCredentialsNetwork() {
    return Network.builder()
        .createNetworkCmdModifier(
            createNetworkCmd -> {
              createNetworkCmd.withDriver("bridge");
              createNetworkCmd.withIpam(
                  new com.github.dockerjava.api.model.Network.Ipam()
                      .withDriver("default")
                      .withConfig(
                          new com.github.dockerjava.api.model.Network.Ipam.Config()
                              .withSubnet("169.254.170.0/24")
                              .withGateway("169.254.170.1")));
            })
        .build();
  }
}
