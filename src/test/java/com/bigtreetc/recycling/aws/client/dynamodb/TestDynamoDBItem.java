package com.bigtreetc.recycling.aws.client.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class TestDynamoDBItem {

  private Integer id;

  private String name;

  @DynamoDbPartitionKey
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @DynamoDbSortKey
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
