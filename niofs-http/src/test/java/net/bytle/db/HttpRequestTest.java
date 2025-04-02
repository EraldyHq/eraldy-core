package net.bytle.db;

import net.bytle.db.connection.Connection;
import net.bytle.db.fs.FsConnection;
import net.bytle.db.fs.FsDataPath;
import net.bytle.db.spi.DataPath;
import net.bytle.db.spi.Tabulars;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpRequestTest {

  @Test
  public void requestViaHttpFile() throws URISyntaxException, MalformedURLException {

    try (Tabular tabular = Tabular.tabular()) {


      FsConnection resource = tabular.createRuntimeConnectionForResources(HttpRequestTest.class, "request");
      FsDataPath request = resource.getDataPath("request.http");
      Assert.assertTrue(Tabulars.exists(request));

      URL website = new URL("https://datacadamia.com/robots.txt");
      Path path = Paths.get(website.toURI());
      Connection connection = tabular.createRuntimeConnectionFromLocalPath("datacadamia", path);

      DataPath requestForConnection =  connection.createScriptDataPath(request);
      DataPath resultDataPath = tabular.getAndCreateRandomMemoryDataPath();
      Tabulars.copy(requestForConnection, resultDataPath);
      String resultString = Tabulars.getString(resultDataPath);
      Assert.assertEquals("",resultString);

    }
  }


}
