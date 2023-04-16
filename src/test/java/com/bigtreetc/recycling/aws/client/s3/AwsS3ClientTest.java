package com.bigtreetc.recycling.aws.client.s3;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.bigtreetc.recycling.BaseTestContainerTest;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class AwsS3ClientTest extends BaseTestContainerTest {

  @Value("${application.aws.s3.bucketName}")
  String s3BucketName;

  @Autowired AwsS3Client awsS3Client;

  @BeforeAll
  public void before() {
    awsS3Client.createBucket(s3BucketName);
  }

  @Test
  @DisplayName("オブジェクトが存在しない場合はFalseが返ること")
  void test1() {
    val actual = awsS3Client.isExistObject(s3BucketName, "notExistFile");
    assertThat(actual).isFalse();
  }

  @Test
  @DisplayName("オブジェクトが保存できること")
  void test2() throws IOException {
    val tempFile = Files.createTempFile("temp-", ".tmp").toFile();
    try (val testOutputStream = new FileOutputStream(tempFile)) {
      testOutputStream.write("test".getBytes(StandardCharsets.UTF_8));
      testOutputStream.flush();
    }

    assertThatCode(
            () -> {
              try (val testInputStream = new FileInputStream(tempFile)) {
                awsS3Client.putObject(s3BucketName, "testFile", testInputStream);
              }
            })
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("オブジェクトが存在する場合はTrueが返ること")
  void test3() {
    val actual = awsS3Client.isExistObject(s3BucketName, "testFile");
    assertThat(actual).isTrue();
  }

  @Test
  @DisplayName("オブジェクトが読み込めること")
  void test4() throws IOException {
    val responseBytes = awsS3Client.getObject(s3BucketName, "testFile");
    val bytes = responseBytes.asByteArray();
    assertThat(new String(bytes)).isEqualTo("test");
  }

  @Test
  @DisplayName("オブジェクトが削除できること")
  void test5() throws IOException {
    assertThatCode(() -> awsS3Client.deleteObject(s3BucketName, "testFile"))
        .doesNotThrowAnyException();
  }
}
