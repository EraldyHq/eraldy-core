package net.bytle.niofs.http;

import net.bytle.exception.IllegalStructure;
import net.bytle.type.UriEnhanced;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class HttpFileTypeDetectorTest {

    @Test
    void wikipediaXmlFileTypeDetectionTest() throws IOException, IllegalStructure {

        URI uriWik = UriEnhanced.createFromString("https://en.wikipedia.org/w/api.php?action=query&titles=SQL&format=xml&prop=description|categories").toUri();

        /**
         * Direct
         */
        URLConnection urlConnection = uriWik.toURL().openConnection();
        String contentType = urlConnection.getContentType();
        String expected = "text/xml; charset=utf-8";
        Assertions.assertEquals(expected, contentType);

        /**
         * Via Path
         */
        Path path = Paths.get(uriWik);
        Assertions.assertEquals(HttpRequestPath.class, path.getClass());
        contentType = Files.probeContentType(path);
        Assertions.assertEquals(expected, contentType);

    }

}