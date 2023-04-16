package com.bigtreetc.recycling.aws.client.ecs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class Network {

  @JsonProperty("NetworkMode")
  String networkMode;

  @JsonProperty("IPv4Addresses")
  List<String> ipAddresses;
}
