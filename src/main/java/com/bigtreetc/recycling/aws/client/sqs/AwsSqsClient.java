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
   * メッセージを送信します。
   *
   * @param queueUrl
   * @param messageGroupId
   * @param message
   * @return
   */
  public String sendMessage(
      final String queueUrl, final String messageGroupId, final String message) {
    Objects.requireNonNull(queueUrl, "queueUrl must not be null");
    Objects.requireNonNull(messageGroupId, "messageGroupId must not be null");
    Objects.requireNonNull(message, "message must not be null");
    log.debug(
        "sendMessage: queueUrl={}, messageGroupId={}, message={}",
        queueUrl,
        messageGroupId,
        message);

    String messageId = null;
    try {
      val request =
          SendMessageRequest.builder()
              .queueUrl(queueUrl)
              .messageBody(message)
              .messageGroupId(messageGroupId) // for FIFO Queue
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
   * メッセージを受信します。
   *
   * @param queueUrl
   * @return
   */
  public List<Message> receiveMessage(final String queueUrl) {
    Objects.requireNonNull(queueUrl, "queueUrl must not be null");
    log.debug("receiveMessage: queueUrl={}", queueUrl);

    try {
      val request =
          ReceiveMessageRequest.builder()
              .visibilityTimeout(configurationProperty.getVisibilityTimeoutSeconds())
              .waitTimeSeconds(configurationProperty.getWaitTimeSeconds())
              .maxNumberOfMessages(configurationProperty.getMaxNumberOfMessages())
              .queueUrl(queueUrl)
              .build();

      val messages = sqsClient.receiveMessage(request).messages();
      log.info("receiveMessage: size={}", messages.size());
      return messages;
    } catch (Exception e) {
      log.error("failed to receive message. ", e);
      throw new AwsClientException("failed to receive message. ", e);
    }
  }

  /**
   * メッセージを削除します。
   *
   * @param queueUrl
   * @param message
   * @return
   */
  public void deleteMessage(final String queueUrl, final Message message) {
    Objects.requireNonNull(queueUrl, "queueUrl must not be null");
    Objects.requireNonNull(message, "message must not be null");

    try {
      val receiptHandle = message.receiptHandle();
      log.debug("deleteMessage: receiptHandle={}", receiptHandle);

      val request =
          DeleteMessageRequest.builder().queueUrl(queueUrl).receiptHandle(receiptHandle).build();

      sqsClient.deleteMessage(request);
      log.info("deleteMessage: receiptHandle={}", receiptHandle);
    } catch (Exception e) {
      log.error("failed to delete message. ", e);
      throw new AwsClientException("failed to delete message. message" + message, e);
    }
  }
}
