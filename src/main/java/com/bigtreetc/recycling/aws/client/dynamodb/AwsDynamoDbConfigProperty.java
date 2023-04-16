package com.bigtreetc.recycling.aws.client.dynamodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.aws.dynamodb")
public class AwsDynamoDbConfigProperty {

  // エンドポイント
  private String endpoint;
}
