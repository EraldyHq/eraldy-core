package net.bytle.xml;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Created by gerard on 09-06-2017.
 */
public class DomsTest {

    /**
     * Test the Doms.getText function
     *
     */
    @Test
    public void xmlGetElementContentTest() {

        InputStream inputStream = this.getClass().getResourceAsStream("/wikipedia/mediawiki.xml");

        String xpath = "/mediawiki/page[1]/title";
        Document doc = Doms.getDom(inputStream);
        NodeList nodeList = Doms.getNodeList(doc, xpath);
        String text = Doms.getText(nodeList.item(0));

        Assertions.assertEquals( "Page title", text,"The content of the node must be the same");

    }

    /**
     * Test the Doms.getText function
     * The document has a namespace
     *
     * @throws Exception
     */
    @Test
    public void xmlGetAttributeContentTest() throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/wikipedia/with_attributes.xml");

        String xpath = "/*[local-name()='DECLARE']/*[local-name()='ConnectionPool']/@type";
        Document doc = Doms.getDom(inputStream);
        NodeList nodeList = Doms.getNodeList(doc, xpath);
        Assertions.assertNotNull( nodeList.item(0),"Something must be returned (nodeList length: " + nodeList.getLength() + ")");

        String text = Doms.getText(nodeList.item(0));

        Assertions.assertEquals( "OCI10G", text,"The content of the node must be the same");


    }

    /**
     * Integration test, updateText a doc, then test it
     * <p>
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
        Assertions.assertNotNull( nodeList.item(0),"Something must be returned (nodeList length: " + nodeList.getLength() + ")");
        String text = Doms.getText(nodeList.item(0));
        Assertions.assertEquals( currentValue, text,"The content of the node must be the same");

        // Update it and write to file
        Doms.updateText(nodeList, updatedValue);
        Path path = File.createTempFile("xml", "xml").toPath();
        Doms.toFile(doc, path);

        // Query it
        inputStream = Files.newInputStream(path);
        doc = Doms.getDom(inputStream);
        nodeList = Doms.getNodeList(doc, xpath);
        Assertions.assertNotNull( nodeList.item(0),"Something must be returned (nodeList length: " + nodeList.getLength() + ")");
        text = Doms.getText(nodeList.item(0));
        Assertions.assertEquals( updatedValue, text,"The content of the node must be the same");

    }
}
