package com.bigtreetc.recycling.aws.client.ecs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class Metadata {

  @JsonProperty("DockerId")
  String dockerId;

  @JsonProperty("Name")
  String name;

  @JsonProperty("DockerName")
  String dockerName;

  @JsonProperty("Image")
  String image;

  @JsonProperty("ImageID")
  String imageId;

  @JsonProperty("Labels")
  Label labels;

  @JsonProperty("DesiredStatus")
  String desiredStatus;

  @JsonProperty("KnownStatus")
  String knownStatus;

  @JsonProperty("Limits")
  Limits limits;

  @JsonProperty("CreatedAt")
  LocalDateTime createdAt;

  @JsonProperty("StartedAt")
  LocalDateTime startedAt;

  @JsonProperty("Type")
  String type;

  @JsonProperty("Networks")
  List<Network> networks;
}
