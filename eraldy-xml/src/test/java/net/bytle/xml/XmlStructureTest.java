package net.bytle.xml;

import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by gerard on 08-10-2016.
 */
public class XmlStructureTest {



    @Test
    public void testPrintNodeWikiTest() throws Exception {

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/wikipedia/with_attributes.xml"));
        new XmlStructure(reader).printNodeNames();
        reader.close();

    }
}
