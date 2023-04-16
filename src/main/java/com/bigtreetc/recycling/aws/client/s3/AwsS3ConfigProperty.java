package com.bigtreetc.recycling.aws.client.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.aws.s3")
public class AwsS3ConfigProperty {

  // エンドポイント
  private String endpoint;
}
