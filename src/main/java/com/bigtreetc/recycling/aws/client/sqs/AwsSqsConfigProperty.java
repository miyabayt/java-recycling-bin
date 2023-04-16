package com.bigtreetc.recycling.aws.client.sqs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.aws.sqs")
public class AwsSqsConfigProperty {

  // エンドポイント
  private String endpoint;

  // 取得メッセージ最大数
  private int maxNumberOfMessages = 1;

  // 可視性タイムアウト時間（秒）
  private int visibilityTimeoutSeconds = 10;

  // メッセージ待機時間（秒）
  private int waitTimeSeconds = 10;
}
