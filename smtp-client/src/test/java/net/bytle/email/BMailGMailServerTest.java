package net.bytle.email;

import jakarta.mail.MessagingException;
import net.bytle.email.test.fixtures.TestSenderUtility;
import net.bytle.os.Oss;
import net.bytle.type.time.Timestamp;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

public class BMailGMailServerTest {


  @Test
  public void sendMail() throws GeneralSecurityException, IOException, MessagingException {


    if (this.isLocalDevMachine()) {
      String date = Timestamp.createFromNowLocalSystem().toIsoString();
      BMailMimeMessage bmail = BMailMimeMessage.createFromBuilder()
        .setFrom("nico@bytle.net")
        .setTo("nico@bytle.net")
        .setSubject("Email Send via Gmail Api + " + date)
        .setBodyPlainText("My Text Body")
        .build();

      TestSenderUtility.createFromMessage(bmail)
        .sendToGmail();

    }

  }

  private boolean isLocalDevMachine() throws UnknownHostException {
    String fqdn = Oss.getFqdn().toStringWithoutRoot();

    String localDevName = "host.docker.internal";
    return fqdn.equals(localDevName);
  }

  @Test
  public void printLabel() throws GeneralSecurityException, IOException {

    BMailGMailServer
      .create()
      .printLabels();

  }

}
