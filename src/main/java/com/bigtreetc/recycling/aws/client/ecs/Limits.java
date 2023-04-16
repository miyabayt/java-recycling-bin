package com.bigtreetc.recycling.aws.client.ecs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Limits {

  @JsonProperty("CPU")
  Integer cpu;

  @JsonProperty("Memory")
  Integer memory;
}
