package com.bigtreetc.recycling.aws.client.cognito;

import java.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@RequiredArgsConstructor
@Slf4j
public class AwsCognitoIdpClient {

  @NonNull final CognitoIdentityProviderClient cognitoIdpClient;

  /**
   * パスワード認証の結果を返します。
   *
   * @param userPoolId
   * @param clientId
   * @param clientSecret
   * @param username
   * @param password
   */
  public AdminInitiateAuthResponse initAuth(
      String userPoolId, String clientId, String clientSecret, String username, String password) {
    val hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, clientSecret).hmac(username + clientId);
    val secretHash = Base64.getEncoder().encodeToString(hmac);

    val authParameters = new HashMap<String, String>();
    authParameters.put("USERNAME", username);
    authParameters.put("PASSWORD", password);
    authParameters.put("SECRET_HASH", secretHash);

    val request =
        AdminInitiateAuthRequest.builder()
            .userPoolId(userPoolId)
            .clientId(clientId)
            .authParameters(authParameters)
            .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
            .build();
    return cognitoIdpClient.adminInitiateAuth(request);
  }

  /**
   * トークンを再取得して返します。
   *
   * @param refreshToken
   * @return
   */
  public AdminInitiateAuthResponse refreshToken(
      String userPoolId, String clientId, String refreshToken) {
    val authParameters = new HashMap<String, String>();
    authParameters.put("REFRESH_TOKEN", refreshToken);

    val request =
        AdminInitiateAuthRequest.builder()
            .userPoolId(userPoolId)
            .clientId(clientId)
            .authParameters(authParameters)
            .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
            .build();
    return cognitoIdpClient.adminInitiateAuth(request);
  }

  /**
   * サインアウトします。
   *
   * @param userPoolId
   * @param username
   * @return
   */
  public AdminUserGlobalSignOutResponse signOut(String userPoolId, String username) {
    val request =
        AdminUserGlobalSignOutRequest.builder().userPoolId(userPoolId).username(username).build();
    return cognitoIdpClient.adminUserGlobalSignOut(request);
  }

  /**
   * ユーザを作成します。
   *
   * @param userPoolId
   * @param clientId
   * @param clientSecret
   * @param username
   * @param password
   * @return
   */
  public AdminCreateUserResponse createUser(
      String userPoolId, String clientId, String clientSecret, String username, String password) {
    return createUser(userPoolId, clientId, clientSecret, username, password, null);
  }

  /**
   * ユーザを作成します。
   *
   * @param userPoolId
   * @param clientId
   * @param clientSecret
   * @param username
   * @param password
   * @param userAttributes
   * @return
   */
  public AdminCreateUserResponse createUser(
      String userPoolId,
      String clientId,
      String clientSecret,
      String username,
      String password,
      Map<String, String> userAttributes) {
    val hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, clientSecret).hmac(username + clientId);
    val secretHash = Base64.getEncoder().encodeToString(hmac);

    val tempPassword = UUID.randomUUID().toString();
    val createUserRequestBuilder =
        AdminCreateUserRequest.builder()
            .userPoolId(userPoolId)
            .username(username)
            .temporaryPassword(tempPassword)
            .messageAction(MessageActionType.SUPPRESS); // メール送信を抑制

    if (userAttributes != null && !userAttributes.isEmpty()) {
      val attributeTypes = new ArrayList<AttributeType>();
      for (Map.Entry<String, String> entry : userAttributes.entrySet()) {
        val key = entry.getKey();
        val value = entry.getValue();
        attributeTypes.add(AttributeType.builder().name(key).value(value).build());
      }
      createUserRequestBuilder.userAttributes(attributeTypes);
    }

    val createUserRequest = createUserRequestBuilder.build();
    val createUserResponse = cognitoIdpClient.adminCreateUser(createUserRequest);

    val authResult = initAuth(userPoolId, clientId, clientSecret, username, tempPassword);
    val challengeResponses = new HashMap<String, String>();
    challengeResponses.put("USERNAME", username);
    challengeResponses.put("NEW_PASSWORD", password);
    challengeResponses.put("SECRET_HASH", secretHash);

    val changePassRequest =
        AdminRespondToAuthChallengeRequest.builder()
            .userPoolId(userPoolId)
            .clientId(clientId)
            .challengeName(authResult.challengeName())
            .session(authResult.session())
            .challengeResponses(challengeResponses)
            .build();
    cognitoIdpClient.adminRespondToAuthChallenge(changePassRequest);

    return createUserResponse;
  }

  /**
   * ユーザを削除します。
   *
   * @param userPoolId
   * @param username
   */
  public void deleteUser(String userPoolId, String username) {
    val request =
        AdminDeleteUserRequest.builder().userPoolId(userPoolId).username(username).build();
    cognitoIdpClient.adminDeleteUser(request);
  }
}
