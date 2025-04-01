package net.bytle.xml;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by gerard on 09-06-2017.
 */
public class DomsTest {

    /**
     * Test the Doms.getText function
     * @throws Exception
     */
    @Test
    public void xmlGetElementContentTest() throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/wikipedia/mediawiki.xml");

        String xpath = "/mediawiki/page[1]/title";
        Document doc = Doms.getDom(inputStream);
        NodeList nodeList = Doms.getNodeList(doc, xpath);
        String text = Doms.getText(nodeList.item(0));

        assertEquals("The content of the node must be the same", "Page title", text);

    }

    /**
     * Test the Doms.getText function
     * The document has a namespace
     * @throws Exception
     */
    @Test
    public void xmlGetAttributeContentTest() throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/wikipedia/with_attributes.xml");

        String xpath = "/*[local-name()='DECLARE']/*[local-name()='ConnectionPool']/@type";
        Document doc = Doms.getDom(inputStream);
        NodeList nodeList = Doms.getNodeList(doc, xpath);
        assertNotNull("Something must be returned (nodeList length: "+nodeList.getLength()+")",  nodeList.item(0));

        String text = Doms.getText(nodeList.item(0));

        assertEquals("The content of the node must be the same", "OCI10G", text);



    }

    /**
     * Integration test, updateText a doc, then test it
     *
     * Get a value from an XML
     * Update the value
     * Write the XML
     * Get the value
     *
     * @throws Exception
     */
    @Test
    public void xmlUpdateAndGetAttributeTest() throws Exception {

        // Parameters
        String xpath = "/*[local-name()='DECLARE']/*[local-name()='ConnectionPool']/@timeout";
        String currentValue = "300";
        String updatedValue = "100";

        // Get the value
        InputStream inputStream = this.getClass().getResourceAsStream("/wikipedia/with_attributes.xml");
        Document doc = Doms.getDom(inputStream);
        NodeList nodeList = Doms.getNodeList(doc, xpath);
        assertNotNull("Something must be returned (nodeList length: "+nodeList.getLength()+")",  nodeList.item(0));
        String text = Doms.getText(nodeList.item(0));
        assertEquals("The content of the node must be the same", currentValue, text);

        // Update it and write to file
        Doms.updateText(nodeList, updatedValue);
        Path path = File.createTempFile("xml","xml").toPath();
        Doms.toFile(doc,path);

        // Query it
        inputStream = Files.newInputStream(path);
        doc = Doms.getDom(inputStream);
        nodeList = Doms.getNodeList(doc, xpath);
        assertNotNull("Something must be returned (nodeList length: "+nodeList.getLength()+")",  nodeList.item(0));
        text = Doms.getText(nodeList.item(0));
        assertEquals("The content of the node must be the same", updatedValue, text);

    }
}
