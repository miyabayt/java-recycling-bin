package com.bigtreetc.recycling;

import com.bigtreetc.recycling.aws.client.cognito.AwsCognitoIdpClient;
import com.bigtreetc.recycling.aws.client.dynamodb.AwsDynamoDBClient;
import com.bigtreetc.recycling.aws.client.ecs.AwsEcsClient;
import com.bigtreetc.recycling.aws.client.s3.AwsS3Client;
import com.bigtreetc.recycling.aws.client.ses.AwsSesClient;
import com.bigtreetc.recycling.aws.client.sqs.AwsSqsClient;
import com.bigtreetc.recycling.aws.client.sqs.AwsSqsConfigProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@Slf4j
public class TestConfig {

  @Bean
  public AwsS3Client awsS3Client(S3Client s3Client) {
    return new AwsS3Client(s3Client);
  }

  @Bean
  public AwsEcsClient awsEcsClient(EcsClient ecsClient) {
    return new AwsEcsClient(ecsClient);
  }

  @Bean
  public AwsDynamoDBClient awsDynamoDBClient(DynamoDbClient dynamoDbClient) {
    return new AwsDynamoDBClient(dynamoDbClient);
  }

  @Bean
  public AwsSqsClient awsSqsClient(AwsSqsConfigProperty properties, SqsClient sqsClient) {
    return new AwsSqsClient(properties, sqsClient);
  }

  @Bean
  public AwsSesClient awsSesClient(SesClient sesClient) {
    return new AwsSesClient(sesClient);
  }

  @Bean
  public AwsCognitoIdpClient awsCognitoIdpClient(CognitoIdentityProviderClient cognitoIdpClient) {
    return new AwsCognitoIdpClient(cognitoIdpClient);
  }
}
