package net.bytle.dns;

import net.bytle.exception.CastException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

class DnsNameTest {

  @Test
  void forwardLookupIpAddressARecord() throws DnsException, DnsNotFoundException, CastException {

    InetAddress inetAddress = XBillDnsClient.createDefault().lookupA("datacadamia.com").getInetAddress();
    System.out.println(inetAddress.getHostName());
    System.out.println(inetAddress.getHostAddress());
    System.out.println(inetAddress.getCanonicalHostName());

  }

  @Test
  void subdomain() throws CastException {
    XBillDnsClient.createDefault().printRecords("gerardnico.github.io");
  }
}
