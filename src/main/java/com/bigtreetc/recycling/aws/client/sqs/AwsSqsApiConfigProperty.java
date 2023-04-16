package com.bigtreetc.recycling.aws.client.sqs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.aws.sqs")
public class AwsSqsApiConfigProperty {

  // エンドポイント
  private String endpoint;

  // メッセージグループID
  private String messageGroupId;

  // 取得メッセージ最大数
  private int maxNumberOfMessages;

  // 接続タイムアウト時間（ms）
  private int timeoutMillis;

  // 可視性タイムアウト時間（秒）
  private int visibilityTimeoutSeconds;

  // メッセージ待機時間（秒）
  private int waitTimeSeconds;
}
