package net.bytle.dns;

import net.bytle.exception.CastException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

class DnsIpAddressTest {

  @Test
  void dnsIpAddressTest() throws DnsException, DnsNotFoundException, CastException {

    XBillDnsClient dnsClient = XBillDnsClient.createDefault();
    String hostName = "www.datacadamia.com";
    InetAddress inetAddress = dnsClient
      .lookupA(hostName)
      .getInetAddress();

    // ip address may change, we can't test the value
    System.out.println(inetAddress.getHostAddress());

  }

}
