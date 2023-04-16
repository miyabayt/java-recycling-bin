package com.bigtreetc.recycling.aws.client.dynamodb;

import com.bigtreetc.recycling.aws.client.AwsClientException;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@RequiredArgsConstructor
@Slf4j
public class AwsDynamoDBClient {

  @NonNull final DynamoDbClient dynamoDbClient;

  /**
   * クエリを実行します。
   *
   * @param clazz
   * @param requestConsumer
   * @param <T>
   * @return
   */
  public <T> PageIterable<T> query(
      String tableName, Consumer<QueryEnhancedRequest.Builder> requestConsumer, Class<T> clazz) {
    if (log.isDebugEnabled()) {
      log.debug("query: tableName={}", tableName);
    }

    try {
      val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
      val table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
      return table.query(requestConsumer);
    } catch (Exception e) {
      throw new AwsClientException("failed to query.", e);
    }
  }

  /**
   * パーティションキーでアイテムを取得します。
   *
   * @param tableName
   * @param partitionValue
   * @param clazz
   * @return
   */
  public <T> Optional<T> getItem(String tableName, Object partitionValue, Class<T> clazz) {
    return getItem(tableName, partitionValue, null, clazz);
  }

  /**
   * パーティションキー、ソートキーでアイテムを取得します。
   *
   * @param tableName
   * @param partitionValue
   * @param sortValue
   * @param clazz
   * @return
   */
  public <T> Optional<T> getItem(
      String tableName, Object partitionValue, Object sortValue, Class<T> clazz) {
    if (log.isDebugEnabled()) {
      log.debug(
          "getItem: tableName={}, partitionValue={}, sortValue={}",
          tableName,
          partitionValue,
          sortValue);
    }

    try {
      val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
      val table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));

      val keyBuilder = Key.builder();
      if (partitionValue instanceof Number number) {
        keyBuilder.partitionValue(number);
      } else {
        keyBuilder.partitionValue((String) partitionValue);
      }
      if (sortValue != null) {
        if (sortValue instanceof Number number) {
          keyBuilder.sortValue(number);
        } else {
          keyBuilder.sortValue((String) sortValue);
        }
      }

      val key = keyBuilder.build();
      val ret = table.getItem(key);
      return Optional.of(ret);
    } catch (ResourceNotFoundException e) {
      return Optional.empty();
    } catch (Exception e) {
      throw new AwsClientException("failed to getItem.", e);
    }
  }

  /**
   * アイテムを保存します。
   *
   * @param item
   * @return
   */
  public <T> void putItem(String tableName, T item, Class<T> clazz) {
    if (log.isDebugEnabled()) {
      log.debug("putItem: tableName={}", tableName);
    }

    try {
      val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
      val table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
      table.putItem(item);
    } catch (Exception e) {
      throw new AwsClientException("failed to putItem.", e);
    }
  }

  /**
   * アイテムを保存します。
   *
   * @param item
   * @return
   */
  public <T> T deleteItem(String tableName, T item, Class<T> clazz) {
    if (log.isDebugEnabled()) {
      log.debug("deleteItem: tableName={}", tableName);
    }

    try {
      val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
      val table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
      val key = table.keyFrom(item);
      log.info(
          "deleteItem: tableName={}, hashKey={}", tableName, key.partitionKeyValue().toString());
      return table.deleteItem(item);
    } catch (Exception e) {
      throw new AwsClientException("failed to deleteItem.", e);
    }
  }

  /**
   * アイテムを一括処理します。
   *
   * @param requestConsumer
   * @return
   */
  public void batchWriteItem(Consumer<BatchWriteItemEnhancedRequest.Builder> requestConsumer) {
    if (log.isDebugEnabled()) {
      log.debug("batchWriteItem: start");
    }

    try {
      val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
      enhancedClient.batchWriteItem(requestConsumer);
      log.info("batchWriteItem: end");
    } catch (Exception e) {
      throw new AwsClientException("failed to batchWriteItem.", e);
    }
  }
}
