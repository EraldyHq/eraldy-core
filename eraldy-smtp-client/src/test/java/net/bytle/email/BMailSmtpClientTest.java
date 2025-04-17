package net.bytle.email;

import jakarta.mail.MessagingException;
import net.bytle.email.test.fixtures.TestSenderUtility;
import net.bytle.email.test.fixtures.WiserBaseTest;
import net.bytle.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.subethamail.wiser.WiserMessage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class BMailSmtpClientTest extends WiserBaseTest {


  public static final String OS_EOL = System.lineSeparator();

  @Test()
  public void name() throws MessagingException, IOException, GeneralSecurityException, NotFoundException {


    String title = "An multi-part mail example";
    String to = "gerardnico@gmail.com";

    String Email_EOL = "\r\n";
    String plainText = "Hallo," + OS_EOL + " This is the text";
    String emailText = "Hallo," + Email_EOL + " This is the text";
    String html = "<H1>Hallo</h1><p>This is the text</p>";
    BMailMimeMessage mimeMessage = BMailMimeMessage.createFromBuilder()
      .setFrom("support@combostrap.com")
      .setTo(to)
      .setSubject(title)
      .setBodyPlainText(plainText)
      .setBodyHtml(html)
      .build();

    TestSenderUtility
      .createAndSendMessageToWiserSmtp(mimeMessage)
      .sendToDotSmtpIfAvailable()
      .sendToMailPitIfAvailable();


    List<WiserMessage> emails = wiser.getMessages();

    assertThat(emails, hasSize(1));

    WiserMessage email = emails.get(0);

    BMailMimeMessage storedBMailMimeMessage = BMailMimeMessage
      .createFromMimeMessage(email.getMimeMessage());

    assertThat(storedBMailMimeMessage.getSubject(), is(title));
    Assertions.assertTrue(storedBMailMimeMessage.getToAsAddresses().toAddresses().get(0).contains(to));
    assertThat(storedBMailMimeMessage.getHtml(), is(html));
    assertThat(storedBMailMimeMessage.getPlainText(), is(emailText));

  }

  @Test()
  public void ping() throws GeneralSecurityException, IOException, MessagingException {
    BMailSmtpClient dotSmtpServer = TestSenderUtility.create()
      .getMailPitClient();
    if(dotSmtpServer!=null){
      dotSmtpServer.pingHello();
    }
  }


}
