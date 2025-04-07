package net.bytle.xml;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by gerard on 01-06-2017.
 */
public class XmlExtractTest {


    @Test
    public void printSubSelectionMultitpleNodeTest() throws Exception {

        Path inputStream = Paths.get("src/test/resources/wikipedia/mediawiki.xml");
        Xmls.xmlExtract(inputStream, "/mediawiki/page/title");

    }

    @Test
    public void printSubSelectionOneNodeTest() throws Exception {

        Path inputStream = Paths.get("src/test/resources//wikipedia/with_attributes.xml");
        Xmls.xmlExtract(inputStream, "/root/ConnectionPool[@name=\"Writeback_Column_Names\"]");

    }


}
