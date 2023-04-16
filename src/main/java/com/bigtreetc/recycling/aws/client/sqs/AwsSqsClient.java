package com.bigtreetc.recycling.aws.client.sqs;

import com.bigtreetc.recycling.aws.client.AwsClientException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@RequiredArgsConstructor
@Slf4j
public class AwsSqsClient {

  @NonNull final AwsSqsConfigProperty configurationProperty;

  @NonNull final SqsClient sqsClient;

  /**
   * キューへメッセージを送信します。
   *
   * @param message
   * @return
   */
  public String sendMessage(final String message) {
    Objects.requireNonNull(message, "message must not be null");
    String messageId = null;

    try {
      log.debug("sendMessage: message={}", message);
      val request =
          SendMessageRequest.builder()
              .queueUrl(configurationProperty.getEndpoint())
              .messageBody(message)
              .messageGroupId(configurationProperty.getMessageGroupId()) // for FIFO Queue
              .messageDeduplicationId(UUID.randomUUID().toString()) // for FIFO Queue
              .build();

      val response = sqsClient.sendMessage(request);
      messageId = response.messageId();
      log.info("sendMessage: messageId={}", messageId);
    } catch (Exception e) {
      log.error("failed to sendMessage. ", e);
      throw new AwsClientException("could not send sqs object. message=" + message, e);
    }

    return messageId;
  }

  /**
   * キューのメッセージを複数取得します。
   *
   * @return
   */
  public List<Message> receiveMessage() {
    try {
      log.debug("receiveMessage: start");
      val request =
          ReceiveMessageRequest.builder()
              .visibilityTimeout(configurationProperty.getVisibilityTimeoutSeconds())
              .waitTimeSeconds(configurationProperty.getWaitTimeSeconds())
              .maxNumberOfMessages(configurationProperty.getMaxNumberOfMessages())
              .queueUrl(configurationProperty.getEndpoint())
              .build();

      val messages = sqsClient.receiveMessage(request).messages();
      log.info("receiveMessage: size={}", messages.size());
      return messages;
    } catch (Exception e) {
      log.error("failed to receiveMessage. ", e);
      throw new AwsClientException("could not receive message. ", e);
    }
  }

  /**
   * キューのメッセージを削除します。
   *
   * @param message
   * @return
   */
  public Message deleteMessage(final Message message) {
    Objects.requireNonNull(message, "message must not be null");

    try {
      val receiptHandle = message.receiptHandle();
      log.debug("deleteMessage: receiptHandle={}", receiptHandle);
      val request =
          DeleteMessageRequest.builder()
              .queueUrl(configurationProperty.getEndpoint())
              .receiptHandle(receiptHandle)
              .build();

      sqsClient.deleteMessage(request);
      log.info("deleteMessage: receiptHandle={}", receiptHandle);
    } catch (Exception e) {
      log.error("failed to deleteMessage. ", e);
      throw new AwsClientException("could not delete message. message" + message, e);
    }

    return message;
  }
}
