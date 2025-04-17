package net.bytle.email.test.fixtures;

import jakarta.mail.MessagingException;
import net.bytle.email.BMailMimeMessage;
import net.bytle.email.BMailSmtpClient;
import net.bytle.email.BMailSmtpConnection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Utility class to send to different smtp server at once
 */
public class TestSenderUtility {

  /**
   * The port of SMTP papercut
   */
  public static final int PORT_GUI_LOCAL_SMTP_SERVER = 25;
  public static final String DEFAULT_FROM_ADDRESS = "from@example.com";
  public static final String DEFAULT_TO_ADDRESS = "to@example.com";
  public static final String DEFAULT_SUBJECT = "Mail Send from Bytle Mail";
  private final BMailSmtpClient wiser;
  private BMailMimeMessage message;
  private BMailSmtpClient mailPitClient;
  private final Map<String, String> osEnvs;


  public TestSenderUtility() throws GeneralSecurityException, IOException {

    wiser = WiserConfiguration.getSession();

    Boolean debug = true;

    osEnvs = System.getenv();


    String mailpitSmtpHost = osEnvs.get("MAILPIT_SMTP_HOST");
    if (mailpitSmtpHost != null) {
      String envPort = osEnvs.get("MAILPIT_SMTP_PORT");
      int mailPitport = 25;
      if (envPort != null) {
        mailPitport = Integer.parseInt(envPort);
      }
      String tlsEnabled = osEnvs.get("MAILPIT_SMTP_TLS");
      boolean isTlsEnabled = mailPitport == 465;
      if (tlsEnabled != null) {
        isTlsEnabled = Boolean.getBoolean(tlsEnabled);
      }
      mailPitClient = BMailSmtpClient.create()
        .setHost(mailpitSmtpHost)
        .setPort(mailPitport)
        .setUsername(osEnvs.get("MAILPIT_SMTP_USER"))
        .setPassword(osEnvs.get("MAILPIT_SMTP_PASSWORD"))
        .setTrustAll(true)
        .setEnableTls(isTlsEnabled)
        .setDebug(debug)
        .build();
    }


  }

  public static TestSenderUtility createAndSendMessageToWiserSmtp(BMailMimeMessage message) throws MessagingException, GeneralSecurityException, IOException {
    return TestSenderUtility
      .create()
      .setMessage(message)
      .sendMessageToLocalWiserSmtpServer();
  }

  public static TestSenderUtility create() throws GeneralSecurityException, IOException {
    return new TestSenderUtility();
  }

  private TestSenderUtility setMessage(BMailMimeMessage message) {
    this.message = message;


    if (message.getToAsAddresses() == null) {
      message.setTo(DEFAULT_TO_ADDRESS);
    }
    if (
      message.getFromAsString() == null) {
      message.setFrom(DEFAULT_FROM_ADDRESS);
    }
    if (
      message.getSubject() == null) {
      message.setSubject(DEFAULT_SUBJECT);
    }
    return this;
  }

  public static TestSenderUtility createFromMessage(BMailMimeMessage bMailMimeMessage) throws GeneralSecurityException, IOException {
    return TestSenderUtility.create().setMessage(bMailMimeMessage);
  }

  @SuppressWarnings("UnusedReturnValue")
  private TestSenderUtility sendMessageToLocalWiserSmtpServer() throws MessagingException {

    try (BMailSmtpConnection transport = wiser.getTransportConnection()) {
      transport.sendMessage(message);
    }

    return this;
  }

  /**
   * Send to the MailPit SMTP server (Papercut, Mailpit, ...)
   * if up
   */
  public TestSenderUtility sendToMailPitIfAvailable() throws MessagingException {


    if (mailPitClient == null) {
      return this;
    }
    try {
      mailPitClient.sendMessage(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
    return this;

  }


  public BMailSmtpClient getWiserSmtpServer() {
    return this.wiser;
  }

  public BMailSmtpClient getMailPitClient() {
    return this.mailPitClient;
  }

}
