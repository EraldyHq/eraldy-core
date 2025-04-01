package net.bytle.dns;

import org.junit.jupiter.api.Test;

class DnsNameServerTest {

  @Test
  void nameLocale() {
    System.out.println(DnsNameServer.getLocale().toString());
  }

}
