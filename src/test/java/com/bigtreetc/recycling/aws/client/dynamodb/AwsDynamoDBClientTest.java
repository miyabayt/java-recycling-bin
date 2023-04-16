package com.bigtreetc.recycling.aws.client.dynamodb;

import static org.junit.jupiter.api.Assertions.*;

import com.bigtreetc.recycling.BaseTestContainerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AwsDynamoDBClientTest extends BaseTestContainerTest {

  @Autowired AwsDynamoDBClient awsDynamoDBClient;

  @Test
  @DisplayName("")
  void test1() {
    // val actual =
    // assertThat(actual).isFalse();
  }
}
