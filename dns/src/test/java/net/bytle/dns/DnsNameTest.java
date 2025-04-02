package net.bytle.dns;

import net.bytle.exception.CastException;
import org.junit.Test;

import java.net.InetAddress;

public class DnsNameTest {

  @Test
  public void forwardLookupIpAddressARecord() throws DnsException, DnsNotFoundException, CastException {

    InetAddress inetAddress = XBillDnsClient.createDefault().lookupA("datacadamia.com").getInetAddress();
    System.out.println(inetAddress.getHostName());
    System.out.println(inetAddress.getHostAddress());
    System.out.println(inetAddress.getCanonicalHostName());

  }

  @Test
  public void subdomain() throws CastException {
    XBillDnsClient.createDefault().printRecords("gerardnico.github.io");
  }
}
