package net.bytle.db;

import net.bytle.db.connection.Connection;
import net.bytle.db.spi.DataPath;
import net.bytle.db.spi.Tabulars;
import net.bytle.type.MediaTypes;
import net.bytle.type.env.DotEnv;
import org.junit.Assert;
import org.junit.Test;

public class HttpMailchimpTest {


  @Test
  public void requestViaResourceAttribute() {

    try (Tabular tabular = Tabular.tabular()) {

      String apiKey = DotEnv.createFromCurrentDirectory().get("api-key");
      Connection mailChimpApi = tabular.createRuntimeConnection("mailchimp", "https://us20.api.mailchimp.com/3.0/")
        .setPassword(apiKey)
        .setUser("anystring");

      DataPath source = mailChimpApi.getDataPath("ping", MediaTypes.TEXT_JSON);


      DataPath target = tabular.getTempFile("tabliTest","ping.json");

      Tabulars.copy(source,target);

      long size = target.getSize();
      Assert.assertTrue("Target File (" + target + ") has a size (" + size + ") bigger than 0", size > 0);

      Tabulars.print(target);

    }

  }

}
