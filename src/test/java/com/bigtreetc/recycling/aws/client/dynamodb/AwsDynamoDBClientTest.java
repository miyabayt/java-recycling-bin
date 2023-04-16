package com.bigtreetc.recycling.aws.client.dynamodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.bigtreetc.recycling.BaseTestContainerTest;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AwsDynamoDBClientTest extends BaseTestContainerTest {

  @Autowired AwsDynamoDBClient awsDynamoDBClient;

  @Autowired DynamoDbClient dynamoDbClient;

  @BeforeAll
  public void before() {
    val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    val table = enhancedClient.table("testTable", TableSchema.fromBean(TestDynamoDBItem.class));
    table.createTable();
  }

  @Test
  @DisplayName("アイテムを保存できること")
  void test1() {
    val item = new TestDynamoDBItem();
    item.setId(1);
    item.setName("John Doe");

    assertThatCode(
            () -> {
              awsDynamoDBClient.putItem("testTable", item, TestDynamoDBItem.class);
            })
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("アイテムを読み込めること")
  void test2() {
    assertThatCode(
            () -> {
              val item =
                  awsDynamoDBClient
                      .getItem("testTable", 1, "John Doe", TestDynamoDBItem.class)
                      .orElseThrow(() -> new RuntimeException("データが見つかりません。"));
              assertThat(item.getId()).isEqualTo(1);
            })
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("クエリが実行できること")
  void test3() {
    assertThatCode(
            () -> {
              val queryConditional =
                  QueryConditional.keyEqualTo(Key.builder().partitionValue(1).build());
              val items =
                  awsDynamoDBClient
                      .query(
                          "testTable",
                          r -> r.queryConditional(queryConditional),
                          TestDynamoDBItem.class)
                      .items();
              val item =
                  items.stream()
                      .findFirst()
                      .orElseThrow(() -> new RuntimeException("データが見つかりません。"));
              assertThat(item.getId()).isEqualTo(1);
            })
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("アイテムを削除できること")
  void test4() {
    assertThatCode(
            () -> {
              val item =
                  awsDynamoDBClient
                      .getItem("testTable", 1, "John Doe", TestDynamoDBItem.class)
                      .orElseThrow(() -> new RuntimeException("データが見つかりません。"));
              val deletedItem =
                  awsDynamoDBClient.deleteItem("testTable", item, TestDynamoDBItem.class);
              assertThat(deletedItem.getId()).isEqualTo(1);
            })
        .doesNotThrowAnyException();
  }
}
