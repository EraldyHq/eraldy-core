package net.bytle.db;

import net.bytle.db.spi.DataPath;
import net.bytle.db.spi.Tabulars;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpDataStoreTest {

  @Test
  public void ssoConnection() {

//    FsConnection dataStore = (FsConnection) FsConnection.createConnectionFromProviderOrDefault("sso", "https://api.godaddy.com")
//      .addProperty("Authorization", "sso-key e52odRf8RFKz_Pmn2CTwRZjdcdUg3abAoCe:QotBm9tBasNdRU22PjGPnY");
//
//    DataPath dataPath = dataStore.getTypedDataPath("json","/v1/domains/availble/tabulle.com");
//    Assert
  }

  @Test
  public void testMove() throws URISyntaxException, MalformedURLException {

    try (Tabular tabular = Tabular.tabular()) {

      URL website = new URL("https://datacadamia.com/robots.txt");
      Path sourcePath = Paths.get(website.toURI());
      DataPath source = tabular.getDataPath(sourcePath);

      Assert.assertTrue("The source exists", Tabulars.exists(source));

      DataPath targetData = tabular.getTempFile("testMove", ".txt");
      Tabulars.dropIfExists(targetData);
      Assert.assertFalse("The target does not exists", Tabulars.exists(targetData));
      String message = "";
      try {
        Tabulars.move(source, targetData);
      } catch (Exception e) {
        message = e.getMessage();
      }
      Assert.assertTrue(message.contains("You can't delete a HTTP file. If you are using a `move` transfer, use the `copy` one instead."));
      Assert.assertTrue("The move has created the target (Fs.move does it)", Tabulars.exists(targetData));


    }


  }

  @Test
  public void testCopy() throws URISyntaxException, MalformedURLException {

    try (Tabular tabular = Tabular.tabular()) {

      URL website = new URL("https://tabulify.com/robots.txt");
      Path sourcePath = Paths.get(website.toURI());
      DataPath source = tabular.getDataPath(sourcePath);

      Assert.assertTrue("The source exists", Tabulars.exists(source));

      DataPath targetData = tabular.getTempFile("testMove", ".txt");
      Tabulars.dropIfExists(targetData);
      Assert.assertFalse("The target does not exists", Tabulars.exists(targetData));

      Tabulars.copy(source, targetData);

      Assert.assertTrue("The copy has created the target", Tabulars.exists(targetData));
      Assert.assertTrue((Long) 2000L < targetData.getSize());
      String content = Tabulars.getString(targetData);
      Assert.assertTrue(content.contains("sitemap"));

    }

  }

}
