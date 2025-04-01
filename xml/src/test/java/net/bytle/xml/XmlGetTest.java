package net.bytle.xml;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by gerard on 08-10-2016.
 */
public class XmlGetTest {


    /**
     * Test the main function
     * @throws Exception
     */
    @Test
    public void xmlGetMainTest() throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/wikipedia/mediawiki.xml");
        String value = Xmls.get(inputStream, "/mediawiki/page[1]/title");
        Assert.assertEquals("Value","Page title",value);

    }


}
