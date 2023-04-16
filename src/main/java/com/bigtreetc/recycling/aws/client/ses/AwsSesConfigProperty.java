package com.bigtreetc.recycling.aws.client.ses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.aws.ses")
public class AwsSesConfigProperty {

  // エンドポイント
  private String endpoint;
}
