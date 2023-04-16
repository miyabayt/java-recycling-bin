package com.bigtreetc.recycling.aws.client.s3;

import com.bigtreetc.recycling.aws.client.AwsClientException;
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
   * オブジェクトが存在する場合はTrueを返します。
   *
   * @param bucketName
   * @param key
   * @return
   */
  public Boolean isExistObject(String bucketName, String key) {
    log.debug("headObject: bucket={}, key={}", bucketName, key);
    val request = HeadObjectRequest.builder().bucket(bucketName).key(key).build();

    try {
      s3Client.headObject(request);
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    } catch (Exception e) {
      throw new AwsClientException(
          "cloud not headObject. [bucketName=" + bucketName + ", key=" + key + "]", e);
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
    log.debug("getObject: bucket={}, key={}", bucketName, key);

    try {
      val request = GetObjectRequest.builder().bucket(bucketName).key(key).build();
      return s3Client.getObject(request, ResponseTransformer.toBytes());
    } catch (Exception e) {
      throw new AwsClientException(
          "cloud not getObject. [bucketName=" + bucketName + ", key=" + key + "]", e);
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
    log.debug("putObject: bucket={}, key={}", bucketName, key);

    try {
      val tempFile = Files.createTempFile("temp-", ".tmp").toFile();
      FileUtils.copyInputStreamToFile(is, tempFile);

      val request = PutObjectRequest.builder().bucket(bucketName).key(key).build();
      s3Client.putObject(request, RequestBody.fromFile(tempFile));
      log.info("putObject: bucketName={}, key={}", bucketName, key);
    } catch (Exception e) {
      throw new AwsClientException(
          "cloud not putObject. [bucketName=" + bucketName + ", key=" + key + "]", e);
    }
  }

  /**
   * オブジェクトを削除します。
   *
   * @param bucketName
   * @param key
   */
  public void deleteObject(String bucketName, String key) {
    log.debug("deleteObject: bucket={}, key={}", bucketName, key);

    try {
      val request = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
      s3Client.deleteObject(request);
      log.info("deleteObject: bucketName={}, key={}", bucketName, key);
    } catch (Exception e) {
      throw new AwsClientException(
          "failed to deleteObject. [bucketName=" + bucketName + ", key=" + key + "]", e);
    }
  }
}
