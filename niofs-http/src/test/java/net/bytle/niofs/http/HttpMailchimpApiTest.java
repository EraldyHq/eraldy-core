package net.bytle.niofs.http;

import net.bytle.fs.Fs;
import net.bytle.type.env.DotEnv;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class HttpMailchimpApiTest {

  @Test
  public void testGetRequestRestApi() throws IOException, URISyntaxException {

    URL website = new URL("https://us20.api.mailchimp.com/3.0/ping");
    String apiKey = DotEnv.createFromCurrentDirectory().get("api-key");
    Path requestPath = Paths.get(website.toURI());
    Files.setAttribute(requestPath, "user", "anystring");
    Files.setAttribute(requestPath, "password", apiKey);
    Path resultPath = Paths.get("build/ping.json");
    if (Files.exists(resultPath)) {
      Files.delete(resultPath);
    }
    Files.copy(requestPath, resultPath, StandardCopyOption.REPLACE_EXISTING);
    long size = Files.size(resultPath);
    Assert.assertTrue("Target File (" + resultPath + ") has a size (" + size + ") bigger than 0", size > 0);
    String result = Fs.getFileContent(resultPath);
    System.out.println(result);

  }

}
