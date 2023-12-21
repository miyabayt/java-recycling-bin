package com.bigtreetc.recycling.aws.client.cognito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AwsCognitoIdpClientTest {

  @Autowired AwsCognitoIdpClient awsCognitoIdpClient;

  @Test
  @DisplayName("存在しないユーザプールを指定した場合は例外が発生すること")
  void test1() {
    assertThatThrownBy(() -> awsCognitoIdpClient.initAuth("ng", "a", "b", "user1", "pass"))
        .isInstanceOf(CognitoIdentityProviderException.class);
  }
}
