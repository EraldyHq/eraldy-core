package net.bytle.dns;

import net.bytle.type.DnsCastException;
import net.bytle.type.DnsName;
import org.junit.Test;


import java.util.List;

public class DnsDomainTest {

  @Test
  public void getGmailMxSync() throws DnsException, DnsNotFoundException, DnsCastException {
    DnsName dnsName = DnsName.create("gmail.com");
    List<DnsMxRecord> records = XBillDnsClient.createDefault().resolveMx(dnsName);
    for (DnsMxRecord mx : records) {
      System.out.println("Host " + mx.getTarget() + " has preference " + mx.getPriority());
    }
  }


  @Test
  public void spfRecord() throws DnsException, DnsNotFoundException, DnsCastException {
    String content = XBillDnsClient.createDefault().lookupSpf(DnsName.create("aol.com"));
    System.out.println(content);
  }
}
