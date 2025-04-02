package net.bytle.email;

import jakarta.mail.internet.AddressException;
import net.bytle.db.Tabular;
import net.bytle.db.fs.FsDataPath;
import net.bytle.db.spi.SelectException;
import net.bytle.db.stream.SelectStream;
import net.bytle.type.Maps;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BMailAddressScoreTest {

  @Ignore
  @Test
  public void basicTest() throws SelectException, AddressException {

    try (Tabular tabular = Tabular.tabular()) {
      final String hash = "80f1bfd55c";
      Path path = Paths.get("C:\\Users\\gerard\\Desktop\\members_export_" + hash + "\\subscribed_members_export_" + hash + ".csv");
      FsDataPath csvTable = tabular.getDataPath(path);
      //Map<BMailAddress, EmailScore> scores = new HashMap<>();
      Map<String, Integer> rootDomainCounts = new HashMap<>();
      try (
        SelectStream csvSelectStream = csvTable.getSelectStream()
      ) {
        Integer count = 0;
        while (csvSelectStream.next()) {
          String emailAddress = csvSelectStream.getString(0);
          BMailInternetAddress email = BMailInternetAddress.of(emailAddress);
          String rootDomain = email.getDomainName().getApexName().toStringWithoutRoot();
          Integer rootDomainCount = rootDomainCounts.get(rootDomain);
          if (rootDomainCount == null) {
            rootDomainCount = 0;
          }
          rootDomainCount++;
          rootDomainCounts.put(rootDomain, rootDomainCount);
          count++;
        }
        Assert.assertEquals("count", (Integer) 924, count);
        Assert.assertEquals("rootDomainCount", 141, rootDomainCounts.keySet().size());
        for (Map.Entry<String, Integer> entry : Maps.getMapAsListEntrySortedByValue(rootDomainCounts)) {
          System.out.println(entry.getKey() + " " + entry.getValue());
        }
      }

    }

  }
}
