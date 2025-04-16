package net.bytle.email;

import jakarta.mail.MessagingException;
import net.bytle.email.test.fixtures.TestSenderUtility;
import net.bytle.email.test.fixtures.WiserBaseTest;
import net.bytle.exception.NotFoundException;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class BMailSmtpClientTest extends WiserBaseTest {


  public static final String EOL = System.lineSeparator();

  @Test
  public void name() throws MessagingException, IOException, GeneralSecurityException, NotFoundException {


    String title = "An multi-part mail example";
    String to = "gerardnico@gmail.com";


    String plainText = "Hallo," + EOL + " This is the text";
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
      .sendToLocalSmtpIfAvailable();


    List<WiserMessage> emails = wiser.getMessages();

    assertThat(emails, hasSize(1));

    WiserMessage email = emails.get(0);

    BMailMimeMessage storedBMailMimeMessage = BMailMimeMessage
      .createFromMimeMessage(email.getMimeMessage());

    assertThat(storedBMailMimeMessage.getSubject(), is(title));
    assertThat(storedBMailMimeMessage.getToAsAddresses(), is(to));
    assertThat(storedBMailMimeMessage.getToAsAddresses(), is(to));
    assertThat(storedBMailMimeMessage.getHtml(), is(html));
    assertThat(storedBMailMimeMessage.getPlainText(), is(plainText));

  }

  @Test
  public void ping() throws GeneralSecurityException, IOException, MessagingException {
    BMailSmtpClient dotSmtpServer = TestSenderUtility.create()
      .getDotSmtpServer();
    if(dotSmtpServer!=null){
      dotSmtpServer.pingHello();
    }
  }


}
