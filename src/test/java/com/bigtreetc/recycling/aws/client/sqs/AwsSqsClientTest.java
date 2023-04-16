package com.bigtreetc.recycling.aws.client.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.bigtreetc.recycling.BaseTestContainerTest;
import java.util.HashMap;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AwsSqsClientTest extends BaseTestContainerTest {

  @Autowired SqsClient sqsClient;

  @Autowired AwsSqsClient awsSqsClient;

  private String queueUrl;

  @BeforeAll
  public void before() {
    Map<String, String> queueAttributes = new HashMap<>();
    queueAttributes.put("FifoQueue", "true");
    queueAttributes.put("ContentBasedDeduplication", "true");
    val createQueueRequest =
        CreateQueueRequest.builder()
            .queueName("testQueue.fifo")
            .attributesWithStrings(queueAttributes)
            .build();
    val createQueueResponse = sqsClient.createQueue(createQueueRequest);
    queueUrl = createQueueResponse.queueUrl();
  }

  @Test
  @DisplayName("メッセージの送信が成功すること")
  void test1() {
    assertThatCode(
            () -> {
              val messageId =
                  awsSqsClient.sendMessage(queueUrl, "testMessageGroupId", "testMessage");
              assertThat(messageId).isNotNull();
            })
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("メッセージの受信が成功すること")
  void test2() {
    assertThatCode(
            () -> {
              val messages = awsSqsClient.receiveMessage(queueUrl);
              assertThat(messages.size()).isGreaterThan(1);
            })
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("メッセージの削除が成功すること")
  void test3() {
    assertThatCode(
            () -> {
              val messages = awsSqsClient.receiveMessage(queueUrl);
              for (val message : messages) {
                awsSqsClient.deleteMessage(queueUrl, message);
              }
            })
        .doesNotThrowAnyException();
  }
}
