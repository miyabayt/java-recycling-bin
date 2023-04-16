package com.bigtreetc.recycling.aws.client.ecs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.aws.ecs")
public class AwsEcsConfigProperty {

  // エンドポイント
  private String endpoint;
}
