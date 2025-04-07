package net.bytle.xml;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

/**
 * Created by gerard on 08-10-2016.
 */
public class XmlUpdateTest {



    @Test
    public void domUpdateTest() throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/wikipedia/mediawiki.xml");
        Xmls.update(inputStream, "/mediawiki/page[1]/title","Hi mom!", System.out, null);
        inputStream.close();

    }
}
