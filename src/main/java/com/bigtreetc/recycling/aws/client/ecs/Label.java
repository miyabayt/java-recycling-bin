package com.bigtreetc.recycling.aws.client.ecs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Label {

  @JsonProperty("com.amazonaws.ecs.cluster")
  String cluster;

  @JsonProperty("com.amazonaws.ecs.container-name")
  String containerName;

  @JsonProperty("com.amazonaws.ecs.task-arn")
  String taskArn;

  @JsonProperty("com.amazonaws.ecs.task-definition-family")
  String taskDefinitionFamily;

  @JsonProperty("com.amazonaws.ecs.task-definition-version")
  String taskDefinitionVersion;
}
