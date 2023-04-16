package com.bigtreetc.recycling;

import com.bigtreetc.recycling.aws.client.dynamodb.AwsDynamoDbConfigProperty;
import com.bigtreetc.recycling.aws.client.ecs.AwsEcsConfigProperty;
import com.bigtreetc.recycling.aws.client.ecs.AwsEcsMetadataClient;
import com.bigtreetc.recycling.aws.client.s3.AwsS3ConfigProperty;
import com.bigtreetc.recycling.aws.client.sqs.AwsSqsApiConfigProperty;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@Slf4j
public class AwsClientConfig {

  @Bean
  @Profile({"local", "test"})
  public AwsCredentialsProvider localAwsCredentialsProvider() {
    log.info("using localAwsCredentialsProvider");
    val awsCredentials = AwsBasicCredentials.create("localstack", "localstack");
    return StaticCredentialsProvider.create(awsCredentials);
  }

  @Bean
  @ConditionalOnMissingBean
  public AwsCredentialsProvider defaultAwsCredentialsProvider() {
    log.info("using defaultAwsCredentialsProvider");
    return DefaultCredentialsProvider.builder().asyncCredentialUpdateEnabled(true).build();
  }

  @Bean
  public S3Client s3Client(
      AwsS3ConfigProperty properties, AwsCredentialsProvider awsCredentialsProvider) {
    val region = Region.AP_NORTHEAST_1;
    val builder = S3Client.builder().credentialsProvider(awsCredentialsProvider).region(region);
    if (properties.getEndpoint() != null) {
      val endpoint = properties.getEndpoint();
      log.info("AWS S3Client endpoint: {}", endpoint);
      builder.endpointOverride(URI.create(endpoint));
    }
    return builder.build();
  }

  @Bean
  public EcsClient ecsClient(
      AwsEcsConfigProperty properties, AwsCredentialsProvider awsCredentialsProvider) {
    val region = Region.AP_NORTHEAST_1;
    val builder = EcsClient.builder().credentialsProvider(awsCredentialsProvider).region(region);
    if (properties.getEndpoint() != null) {
      val endpoint = properties.getEndpoint();
      log.info("AWS EcsClient endpoint: {}", endpoint);
      builder.endpointOverride(URI.create(endpoint));
    }
    return builder.build();
  }

  @Bean
  public DynamoDbClient dynamoDbClient(
      AwsDynamoDbConfigProperty properties, AwsCredentialsProvider awsCredentialsProvider) {
    val region = Region.AP_NORTHEAST_1;
    val builder =
        DynamoDbClient.builder().credentialsProvider(awsCredentialsProvider).region(region);
    if (properties.getEndpoint() != null) {
      val endpoint = properties.getEndpoint();
      log.info("AWS DynamoDbClient endpoint: {}", endpoint);
      builder.endpointOverride(URI.create(endpoint));
    }
    return builder.build();
  }

  @Bean
  public SqsClient sqsClient(
      AwsSqsApiConfigProperty properties, AwsCredentialsProvider awsCredentialsProvider) {
    val region = Region.AP_NORTHEAST_1;
    val builder = SqsClient.builder().credentialsProvider(awsCredentialsProvider).region(region);
    if (properties.getEndpoint() != null) {
      val endpoint = properties.getEndpoint();
      log.info("AWS SqsClient endpoint: {}", endpoint);
      builder.endpointOverride(URI.create(endpoint));
    }
    return builder.build();
  }

  @Bean
  public AwsEcsMetadataClient awsEcsMetadataClient(
      Environment environment, RestTemplateBuilder restTemplateBuilder) {
    val restTemplate = restTemplateBuilder.build();
    return new AwsEcsMetadataClient(environment, restTemplate);
  }
}
