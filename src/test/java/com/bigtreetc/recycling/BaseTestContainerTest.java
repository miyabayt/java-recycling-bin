package com.bigtreetc.recycling;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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

  static {
    LOCALSTACK_CONTAINER.start();
  }

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("application.aws.s3.endpoint", getLocalStackEndpoint(Service.S3));
    registry.add("application.aws.dynamodb.endpoint", getLocalStackEndpoint(Service.DYNAMODB));
    registry.add("application.aws.sqs.endpoint", getLocalStackEndpoint(Service.SQS));
    registry.add("application.aws.ses.endpoint", getLocalStackEndpoint(Service.SES));
  }

  @NotNull
  private static Supplier<Object> getLocalStackEndpoint(Service service) {
    return () -> LOCALSTACK_CONTAINER.getEndpointOverride(service);
  }
}
