package net.bytle.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import net.bytle.email.test.fixtures.TestSenderUtility;
import net.bytle.email.test.fixtures.WiserBaseTest;
import net.bytle.exception.NotFoundException;
import net.bytle.fs.Fs;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class BMailMimeMessageTest extends WiserBaseTest {

  public static final String TO_ADDRESS = "to@example.com";
  public static final String FROM_ADDRESS = "from@example.com";

  String message = "Date: Mon, 8 Feb 2021 21:12:01 +0100 (CET)\n" +
    "From: " + FROM_ADDRESS + "\n" +
    "To: " + TO_ADDRESS + "\n" +
    "Message-ID: <171687780.3.1612815121479@host.docker.internal>\n" +
    "Subject: An multi-part mail example\n" +
    "MIME-Version: 1.0\n" +
    "Content-Type: multipart/alternative; \n" +
    "\tboundary=\"----=_Part_2_1636610998.1612815121479\"\n" +
    "\n" +
    "------=_Part_2_1636610998.1612815121479\n" +
    "Content-Type: text/plain; charset=utf-8\n" +
    "Content-Transfer-Encoding: 7bit\n" +
    "\n" +
    "Hallo,\n" +
    " This is the text\n" +
    "------=_Part_2_1636610998.1612815121479\n" +
    "Content-Type: text/html; charset=utf-8\n" +
    "Content-Transfer-Encoding: 7bit\n" +
    "\n" +
    "<H1>Hallo</h1><p>This is the text</p>\n" +
    "------=_Part_2_1636610998.1612815121479--";

  public static final String EOL = System.lineSeparator();


  @Test()
  public void toRawTextTest() throws MessagingException, IOException, NotFoundException {


    String title = "An multi-part mail example";
    String to = "gerardnico@gmail.com";


    String plainText = "Hallo," + EOL + " This is the text";
    String html = "<H1>Hallo</h1><p>This is the text</p>";
    String mimeMessage = BMailMimeMessage.createFromBuilder()
      .setFrom("support@combostrap.com")
      .setTo(to)
      .setSubject(title)
      .setBodyPlainText(plainText)
      .setBodyHtml(html)
      .build()
      .toEml();

    BMailMimeMessage bMailMimeMessage = BMailMimeMessage.createFromEml(mimeMessage);

    assertThat(to, is(bMailMimeMessage.getToAsAddresses()));
    assertThat(title, is(bMailMimeMessage.getSubject()));
    assertThat(html, is(bMailMimeMessage.getHtml()));
    assertThat(plainText, is(bMailMimeMessage.getPlainText()));


    Path path = Fs.getUserDesktop().resolve("mail.eml");
    Fs.write(path, mimeMessage);
    System.out.println("Message was written at " + path);


  }

  @Test()
  public void toRawTextWithAttachmentTest() throws MessagingException, IOException, GeneralSecurityException {


    String title = "An multi-part mail example";
    String to = "gerardnico@gmail.com";

    Path attach = Fs.getTempDirectory().resolve("attachment.txt");
    Fs.deleteIfExists(attach);
    Fs.write(attach, "The attachment");

    String plainText = "Hallo," + EOL + " This is the email with one attachment.";
    BMailMimeMessage bMailMimeMessageFirst = BMailMimeMessage
      .createFromBuilder()
      .setFrom("support@combostrap.com")
      .setTo(to)
      .setSubject(title)
      .setBodyPlainText(plainText)
      .setBodyHtml("<html><body>Hallo</body></html>")
      .addAttachment(attach)
      .build();

    /**
     * To mime encoded and back
     */
    String mimeMessage = bMailMimeMessageFirst.toEml();
    Path path = Fs.getUserDesktop().resolve("mail-before.eml");
    Fs.write(path, mimeMessage);
    System.out.println("Message was written at " + path);

    assertThat(mimeMessage.contains("multipart"),is(true));
    assertThat(mimeMessage.contains("Content-Disposition: attachment; filename=attachment.txt"),is(true));
//    String mimeMessageWithoutTimedHeadersToCompare = bMailMimeMessageFirst.toRawTextFormatWithoutHeaders("Date","Message-ID");
//    Path pathWithoutTimedHeaders = Fs.getUserDesktop().resolve("mail-without-timed-headers.eml");
//    Fs.write(pathWithoutTimedHeaders, mimeMessageWithoutTimedHeadersToCompare);
//    System.out.println("Message was written at " + pathWithoutTimedHeaders);
//    assertThat(mimeMessageWithoutTimedHeadersToCompare, is(rawEncodedMultipart));

    /**
     * Can we send it
     */
    TestSenderUtility.createAndSendMessageToWiserSmtp(bMailMimeMessageFirst)
      .sendToLocalSmtpIfAvailable();


    /**
     * Reading a multipart does not work
     * (problem with carriage return, it seems but yeah)
     */


//    BMailMimeMessage bMailMimeMessageFromMimeText = BMailMimeMessage.createFromRawTextNative(mimeMessage);
//
//    Path pathAfter = Fs.getUserDesktop().resolve("mail-after.eml");
//    Fs.write(pathAfter, bMailMimeMessageFromMimeText.toRawTextFormat());
//    System.out.println("Message was written at " + pathAfter);
//
//    assertThat(bMailMimeMessageFromMimeText.getContentType(), is("multipart/alternative"));
//    assertThat(to, is(bMailMimeMessageFromMimeText.getTo()));
//    assertThat(title, is(bMailMimeMessageFromMimeText.getSubject()));
//    assertThat(false, is(bMailMimeMessageFromMimeText.hasNoContent()));
//    assertThat(bMailMimeMessageFromMimeText.getPlainText(), is(plainText));
//    //assertThat(bMailMimeMessageFromMimeText.getAttachmentSize(), is(1));
//
//
//
//
//
//
//    TestSenderUtility.createAndSendMessageToWiserSmtp(bMailMimeMessageFromMimeText)
//      .sendToLocalSmtpIfAvailable();


  }


  /**
   * Shows how it's possible to parse a mime with the default
   * library
   *
   * @throws IOException        if io exception
   * @throws MessagingException if message exception
   */
  @Test()
  public void createFromRawText() throws IOException, MessagingException {


    BMailMimeMessage bmailMimeMessage = BMailMimeMessage.createFromEml(message);
    MimeMessage mimeMessage = bmailMimeMessage.toMimeMessage();
    assertThat(mimeMessage.getContentType(), startsWith("multipart/alternative"));
    assertThat(mimeMessage.getContent(), instanceOf(Multipart.class));
    Multipart multipart = (Multipart) mimeMessage.getContent();
    assertThat(multipart.getCount(), is(2));

  }
}
