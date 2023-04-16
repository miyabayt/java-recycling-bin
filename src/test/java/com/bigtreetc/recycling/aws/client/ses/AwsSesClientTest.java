package com.bigtreetc.recycling.aws.client.ses;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.bigtreetc.recycling.BaseTestContainerTest;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AwsSesClientTest extends BaseTestContainerTest {

  @Autowired AwsSesClient awsSesClient;

  @Test
  @DisplayName("メールアドレス検証が成功すること")
  void test1() {
    val from = "from@example.com";
    assertThatCode(() -> awsSesClient.verifyEmailIdentity(from)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("メール送信が成功すること")
  void test2() {
    val from = "from@example.com";
    val to = "to@example.com";
    val subject = "testSubject";
    val bodyText = "body";

    assertThatCode(() -> awsSesClient.sendMail(from, to, subject, bodyText))
        .doesNotThrowAnyException();
  }
}
