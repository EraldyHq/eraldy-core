package net.bytle.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

/**
 * Created by gerard on 08-10-2016.
 */
public class XmlGetTest {


    /**
     * Test the main function
     */
    @Test
    public void xmlGetMainTest() {

        InputStream inputStream = this.getClass().getResourceAsStream("/wikipedia/mediawiki.xml");
        String value = Xmls.get(inputStream, "/mediawiki/page[1]/title");
        Assertions.assertEquals("Page title",value,"Value");

    }


}
