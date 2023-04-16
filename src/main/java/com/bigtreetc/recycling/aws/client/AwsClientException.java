package com.bigtreetc.recycling.aws.client;

/** AWS Clientエラー */
public class AwsClientException extends RuntimeException {

  private static final long serialVersionUID = 2395411283566671922L;

  /** コンストラクタ */
  public AwsClientException(String message) {
    super(message);
  }

  /** コンストラクタ */
  public AwsClientException(String message, Exception e) {
    super(message, e);
  }

  /** コンストラクタ */
  public AwsClientException(Exception e) {
    super(e);
  }
}
