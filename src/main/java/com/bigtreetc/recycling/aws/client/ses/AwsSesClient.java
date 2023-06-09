package com.bigtreetc.recycling.aws.client.ses;

import com.bigtreetc.recycling.aws.client.AwsClientException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@RequiredArgsConstructor
@Slf4j
public class AwsSesClient {

  @NonNull final SesClient sesClient;

  /**
   * メールアドレスを検証します。
   *
   * @param email
   */
  public void verifyEmailIdentity(String email) {
    if (log.isDebugEnabled()) {
      log.debug("verifyEmailIdentity [email={}]", email);
    }

    val request = VerifyEmailIdentityRequest.builder().emailAddress(email).build();

    try {
      sesClient.verifyEmailIdentity(request);
    } catch (Exception e) {
      throw new AwsClientException("failed to verify email identity.", e);
    }
  }

  /**
   * メールを送信します。
   *
   * @param from
   * @param to
   * @param subject
   * @param bodyText
   */
  public void sendMail(String from, String to, String subject, String bodyText) {
    if (log.isDebugEnabled()) {
      log.debug("sendMail [from={}, to={}, subject={}]", from, to, subject);
    }

    val destination = Destination.builder().toAddresses(to).build();
    val subjectContent = Content.builder().data(subject).build();
    val bodyContent = Content.builder().data(bodyText).build();
    val body = Body.builder().text(bodyContent).build();
    val message = Message.builder().subject(subjectContent).body(body).build();

    val request =
        SendEmailRequest.builder().source(from).destination(destination).message(message).build();

    try {
      sesClient.sendEmail(request);
    } catch (Exception e) {
      throw new AwsClientException("failed to send email.", e);
    }
  }
}
