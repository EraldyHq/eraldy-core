package net.bytle.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class NamespaceContextMapTest {

    @Test
    public void testContext() {
        Map<String, String> mappings = new HashMap<String, String>();
        mappings.put("foo", "http://foo");
        mappings.put("altfoo", "http://foo");
        mappings.put("bar", "http://bar");
        mappings.put(XMLConstants.XML_NS_PREFIX,
                XMLConstants.XML_NS_URI);

        NamespaceContext context = new NamespaceContextMap(
                mappings);
        for (Map.Entry<String, String> entry : mappings
                .entrySet()) {
            String prefix = entry.getKey();
            String namespaceURI = entry.getValue();

            Assertions.assertEquals( namespaceURI,
                    context.getNamespaceURI(prefix),"namespaceURI");
            boolean found = false;
            Iterator<?> prefixes = context
                    .getPrefixes(namespaceURI);
            while (prefixes.hasNext()) {
                if (prefix.equals(prefixes.next())) {
                    found = true;
                    break;
                }
                try {
                    prefixes.remove();
                    Assertions.fail("rw");
                } catch (UnsupportedOperationException e) {
                }
            }
            Assertions.assertTrue(found, "prefix: " + prefix);
            Assertions.assertNotNull("prefix: " + prefix, context
                    .getPrefix(namespaceURI));
        }

        Map<String, String> ctxtMap = ((NamespaceContextMap) context)
                .getMap();
        for (Map.Entry<String, String> entry : mappings
                .entrySet()) {
            Assertions.assertEquals(entry.getValue(), ctxtMap
                    .get(entry.getKey()));
        }

        System.out.println(context);
    }

    @Test
    public void testModify() {
        NamespaceContextMap context = new NamespaceContextMap();

        try {
            Map<String, String> ctxtMap = context.getMap();
            ctxtMap.put("a", "b");
            Assertions.fail("rw");
        } catch (UnsupportedOperationException e) {
        }

        try {
            Iterator<String> it = context
                    .getPrefixes(XMLConstants.XML_NS_URI);
            it.next();
            it.remove();
            Assertions.fail("rw");
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void testConstants() {
        NamespaceContext context = new NamespaceContextMap();
        Assertions.assertEquals(XMLConstants.XML_NS_URI, context
                .getNamespaceURI(XMLConstants.XML_NS_PREFIX));
        Assertions.assertEquals(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI, context
                        .getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE));
        Assertions.assertEquals(XMLConstants.XML_NS_PREFIX, context
                .getPrefix(XMLConstants.XML_NS_URI));
        Assertions.assertEquals(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI, context
                        .getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE));
    }

}