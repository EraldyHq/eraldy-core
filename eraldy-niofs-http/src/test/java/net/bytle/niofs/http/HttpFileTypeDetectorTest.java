package net.bytle.niofs.http;

import net.bytle.exception.IllegalStructure;
import net.bytle.type.UriEnhanced;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class HttpFileTypeDetectorTest {

  @Test
  void wikipediaXmlFileTypeDetectionTest() throws IOException, IllegalStructure, URISyntaxException {

    URI uri = UriEnhanced.createFromString("https://en.wikipedia.org/w/api.php?action=query&titles=SQL&format=xml&prop=description|categories").toUri();
    HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
    conn.setRequestProperty(HttpHeader.USER_AGENT.toKeyNormalizer().toHttpHeaderCase(), HttpHeader.USER_AGENT.toString());
    conn.setRequestMethod("GET");
    // Check response code
    int responseCode = conn.getResponseCode();
    Assertions.assertEquals(200, responseCode);
    String contentType = conn.getContentType();
    String expected = "text/xml; charset=utf-8";
    Assertions.assertEquals(expected, contentType);
    conn.disconnect();

    /**
     * Via Path
     */
    Path path = Paths.get(uri);
    Assertions.assertEquals(HttpPath.class, path.getClass());
    contentType = Files.probeContentType(path);
    Assertions.assertEquals(expected, contentType);

  }

}
