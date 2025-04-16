package net.bytle.email;

import jakarta.mail.MessagingException;
import net.bytle.email.test.fixtures.TestSenderUtility;
import net.bytle.email.test.fixtures.WiserBaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class BMailTransactionalTemplateTest extends WiserBaseTest {

  @Test()
  public void base() throws GeneralSecurityException, IOException, MessagingException {

    String subject = "Email Account confirmation";
    BMailTransactionalTemplate emailCreator = BMailTransactionalTemplate
      .createFromPath("simple/simple")
      .setPreview("Welcome !")
      .setTitle(subject)
      .setSalutation("Hy")
      .setRecipientName("Nico")
      .addIntroParagraph("We're very excited to have you on board.")
      .setActionUrl("https://combostrap.com")
      .setActionDescription("To get started, click the link below:")
      .setActionName("Create your account")
      .setActionIsGo(true)
      .addOutroParagraph("Need help, or have questions? Just reply to this email, we'd love to help.")
      .setValediction("Cheers")
      .setSenderName("EmailCreator")
      .setBrandLogo("https://combostrap.com/_media/android-chrome-192x192.png")
      .setBrandName("ComboStrap")
      .setBrandUrl("https://combostrap.com")
      .addPostScriptum("The publication platform")
      .setBrandLogoWidth("25px")
      .setPrimaryColor("#3498db");


    String html = emailCreator.generateHTML();
    String text = emailCreator.generatePlainText();

    BMailMimeMessage.builder message = BMailMimeMessage
      .createFromBuilder()
      .setSubject(subject)
      .setBodyHtml(html);
    if (text != null) {
      message.setBodyPlainText(text);
    }

    TestSenderUtility
      .createAndSendMessageToWiserSmtp(message.build())
      .sendToLocalSmtpIfAvailable()
      .sendToDotSmtpIfAvailable();

  }

  @Test()
  public void accountCreation() {
    BMailTransactionalTemplate.createForAccountCreation();
  }
}
