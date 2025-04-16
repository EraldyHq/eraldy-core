package net.bytle.email;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class BMailDeliverEmail {

  /**
   * To overcome the provider firewall:
   * - wireguard
   * - or socks proxy
   * <a href="https://jakartaee.github.io/mail-api/FAQ.html#pr<a href="oxy">...</a>
   ">* https://en.wikipe</a>dia.org/wiki/SOCKS
   */
  @Disabled
  @Test()
  public void gmailSendEmailTest() throws MessagingException {
    // Host alt4.gmail-smtp-in.l.google.com. has preference 40
    // Host alt3.gmail-smtp-in.l.google.com. has preference 30
    // Host alt2.gmail-smtp-in.l.google.com. has preference 20
    // Host gmail-smtp-in.l.google.com. has preference 5
    // Host alt1.gmail-smtp-in.l.google.com. has preference 10
    BMailSmtpClient gmailSmtpServer = BMailSmtpClient.create()
      .setHost("alt1.gmail-smtp-in.l.google.com")
      .setPort(25)
      .setStartTls(BMailStartTls.REQUIRE)
      .build();
    BMailMimeMessage email = BMailMimeMessage.createFromBuilder()
      .setFrom("nico@bytle.net")
      .setTo("gerardnico@gmail.com")
      .setSubject("Second test from my computer")
      .setBodyPlainText("Is this going to work ?")
      .build();
    gmailSmtpServer.sendMessage(email);

  }

}
