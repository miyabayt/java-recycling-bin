package com.bigtreetc.recycling.aws.client.ecs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
public class AwsEcsMetadataClient {

  @NonNull final Environment environment;

  @NonNull final RestTemplate restTemplate;

  private static final String METADATA_URI_ENV = "ECS_CONTAINER_METADATA_URI";

  /**
   * コンテナメタデータを取得します。
   *
   * @return
   */
  public Metadata getMetadata() {
    val metadataUri = environment.getProperty(METADATA_URI_ENV);
    if (metadataUri == null) {
      throw new IllegalStateException("environment variable 'ECS_CONTAINER_METADATA_URI' not set");
    }

    return restTemplate.getForObject(metadataUri, Metadata.class);
  }
}
