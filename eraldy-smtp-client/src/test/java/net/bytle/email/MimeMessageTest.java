package net.bytle.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import net.bytle.type.Strings;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class MimeMessageTest {

  @Test
  public void contentIsEmptyTest() throws MessagingException {
    Session session = Session.getInstance(new Properties());
    MimeMessage mimeMessage = new MimeMessage(session);
    assertThat(mimeMessage.getContentMD5(), IsNull.nullValue());
  }

  /**
   * This test shows what the result of input stream
   * and how to get the raw data
   * @throws IOException io exception
   * @throws MessagingException messaging exception
   */
  @Test
  public void rawDataContentStreamTest() throws IOException, MessagingException {
    Session session = Session.getInstance(new Properties());
    MimeMessage mimeMessage = new MimeMessage(session);
    mimeMessage.setRecipients(MimeMessage.RecipientType.TO, "to@example.com");

    /**
     * Mime message is mandatory on all inputstream function
     */
    String message = null;
    try {
      /**
       * Get input stream returns just the body
       */
      mimeMessage.getInputStream();
    } catch (IOException e){
      message = e.getMessage();
    }
    assertThat(message, is("No MimeMessage content"));

    try {
      /**
       * Get raw input stream returns also just the body
       */
      mimeMessage.getRawInputStream();
    } catch (MessagingException e){
      message = e.getMessage();
    }
    assertThat(message, is("No MimeMessage content"));

    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    try {

      mimeMessage.writeTo(boas);
    } catch (IOException e){
      message = e.getMessage();
    }
    assertThat(message, is("No MimeMessage content"));

    /**
     * Adding message content
     */
    String messageContent = "plain";
    mimeMessage.setContent(messageContent,"text/plain");


    /**
     * We get the content
     * via the input stream
     */
    String is = Strings.createFromInputStream(mimeMessage.getInputStream()).toString();
    assertThat(is, is(messageContent));

    /**
     * We get the content
     * via getContent
     */
    is = mimeMessage.getContent().toString();
    assertThat(is, is(messageContent));

    /**
     * Raw data
     */
    mimeMessage.writeTo(boas);
    String raw = boas.toString(Charset.defaultCharset());
    assertThat(raw, startsWith("Date:"));

    /**
     * Copy
     */
    MimeMessage copyMime = new MimeMessage(session, new ByteArrayInputStream(boas.toByteArray()));
    ByteArrayOutputStream copyBoas = new ByteArrayOutputStream();
    copyMime.writeTo(copyBoas);
    String copyRaw = copyBoas.toString(Charset.defaultCharset());
    assertThat(raw, is(copyRaw));

  }
}
