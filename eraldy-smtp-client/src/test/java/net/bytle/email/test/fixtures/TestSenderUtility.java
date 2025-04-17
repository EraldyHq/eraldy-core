package net.bytle.email.test.fixtures;

import jakarta.mail.MessagingException;
import net.bytle.email.BMailMimeMessage;
import net.bytle.email.BMailSmtpClient;
import net.bytle.email.BMailSmtpConnection;
import net.bytle.email.BMailStartTls;
import net.bytle.exception.CastException;
import net.bytle.java.JavaEnvs;
import net.bytle.os.Oss;
import net.bytle.type.Casts;

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
  private final BMailSmtpClient envSmtpServer;
  private final Map<String, String> osEnvs;
  private final BMailSmtpClient paperCutLocalSmtpServer;

  public TestSenderUtility() throws GeneralSecurityException, IOException {

    wiser = WiserConfiguration.getSession();


    osEnvs = System.getenv();
    Boolean debug;
    String smtpDebug = osEnvs.get("SMTP_DEBUG");
    try {
      debug = Casts.cast(smtpDebug, Boolean.class);
    } catch (CastException e) {
      throw new RuntimeException("The debug value " + smtpDebug + " of SMTP_DEBUG is not a valid boolean");
    }
    String smtpUser = osEnvs.get("SMTP_USER");
    if (smtpUser != null) {
      envSmtpServer = BMailSmtpClient.create()
        .setHost(osEnvs.get("SMTP_HOST"))
        .setPort(Integer.parseInt(osEnvs.get("SMTP_PORT")))
        .setUsername(smtpUser)
        .setPassword(osEnvs.get("SMTP_PWD"))
        .setStartTls(BMailStartTls.REQUIRE)
        .setDebug(debug)
        .build();
    } else {
      envSmtpServer = null;
    }

    if (!Oss.portAvailable(PORT_GUI_LOCAL_SMTP_SERVER)) {
      paperCutLocalSmtpServer = BMailSmtpClient.create()
        .setPort(PORT_GUI_LOCAL_SMTP_SERVER)
        .build();
    } else {
      if (JavaEnvs.isDev(TestSenderUtility.class)) {
        throw new RuntimeException("The local development server (Papercut) is not started, you can send an email");
      }
      paperCutLocalSmtpServer = null;
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
   * Send to the local SMTP server (Papercut)
   * if up
   */
  public TestSenderUtility sendToLocalSmtpIfAvailable() throws MessagingException {

    // Not null if port is running
    if (paperCutLocalSmtpServer == null) {
      return this;
    }
    try {
      paperCutLocalSmtpServer.sendMessage(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
    return this;

  }

  /**
   * Send the message specified in a dot file if
   */
  @SuppressWarnings("UnusedReturnValue")
  public TestSenderUtility sendToDotSmtpIfAvailable() throws MessagingException {

    if (envSmtpServer == null) {
      return this;
    }

    BMailMimeMessage localMessage = BMailMimeMessage.createFromBMail(message)
      .setFrom(osEnvs.get("SMTP_FROM"))
      .setTo(osEnvs.get("SMTP_TO"));

    envSmtpServer.sendMessage(localMessage);

    return this;

  }

  public BMailSmtpClient getPaperCutSmtpServer() {
    return this.paperCutLocalSmtpServer;
  }

  public BMailSmtpClient getWiserSmtpServer() {
    return this.wiser;
  }

  public BMailSmtpClient getEnvSmtpServer() {
    return this.envSmtpServer;
  }

}
