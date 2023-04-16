package com.bigtreetc.recycling.aws.client.s3;

import com.bigtreetc.recycling.aws.client.AwsClientException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@RequiredArgsConstructor
@Slf4j
public class AwsS3Client {

  @NonNull final S3Client s3Client;

  /**
   * バケットを作成します。
   *
   * @param bucketName
   * @return
   */
  public CreateBucketResponse createBucket(String bucketName) {
    log.debug("s3createBucket: bucket={}", bucketName);

    try {
      val request = CreateBucketRequest.builder().bucket(bucketName).build();
      return s3Client.createBucket(request);
    } catch (Exception e) {
      throw new AwsClientException("failed to create bucket. [bucketName=" + bucketName + "]", e);
    }
  }

  /**
   * オブジェクトが存在する場合はTrueを返します。
   *
   * @param bucketName
   * @param key
   * @return
   */
  public Boolean isExistObject(String bucketName, String key) {
    log.debug("s3headObject: bucket={}, key={}", bucketName, key);

    try {
      val request = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
      s3Client.headObject(request);
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    } catch (Exception e) {
      throw new AwsClientException(
          "failed to head object. [bucketName=" + bucketName + ", key=" + key + "]", e);
    }
  }

  /**
   * オブジェクトを読み込みます。
   *
   * @param bucketName
   * @param key
   * @return
   */
  public ResponseBytes<GetObjectResponse> getObject(String bucketName, String key) {
    log.debug("s3getObject: bucket={}, key={}", bucketName, key);

    try {
      val request = GetObjectRequest.builder().bucket(bucketName).key(key).build();
      return s3Client.getObject(request, ResponseTransformer.toBytes());
    } catch (Exception e) {
      throw new AwsClientException(
          "failed to  get object. [bucketName=" + bucketName + ", key=" + key + "]", e);
    }
  }

  /**
   * オブジェクトを保存します。
   *
   * @param bucketName
   * @param key
   * @param is
   */
  public void putObject(String bucketName, String key, InputStream is) {
    log.debug("s3putObject: bucket={}, key={}", bucketName, key);

    File tempFile = null;
    try {
      tempFile = Files.createTempFile("temp-", ".tmp").toFile();
      FileUtils.copyInputStreamToFile(is, tempFile);

      val request = PutObjectRequest.builder().bucket(bucketName).key(key).build();
      s3Client.putObject(request, RequestBody.fromFile(tempFile));
      log.info("s3putObject: bucketName={}, key={}", bucketName, key);
    } catch (Exception e) {
      throw new AwsClientException(
          "failed to put object. [bucketName=" + bucketName + ", key=" + key + "]", e);
    } finally {
      if (tempFile != null) {
        try {
          Files.deleteIfExists(tempFile.toPath());
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }

  /**
   * オブジェクトを削除します。
   *
   * @param bucketName
   * @param key
   */
  public void deleteObject(String bucketName, String key) {
    log.debug("s3deleteObject: bucket={}, key={}", bucketName, key);

    try {
      val request = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
      s3Client.deleteObject(request);
      log.info("s3deleteObject: bucketName={}, key={}", bucketName, key);
    } catch (Exception e) {
      throw new AwsClientException(
          "failed to delete object. [bucketName=" + bucketName + ", key=" + key + "]", e);
    }
  }
}
