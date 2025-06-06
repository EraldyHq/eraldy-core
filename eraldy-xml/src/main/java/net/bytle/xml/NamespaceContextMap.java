package net.bytle.xml;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.*;


/**
 * There are no implementations of NamespaceContext provided in the standard library
 *
 * An implementation of <a
 * href="http://java.sun.com/javase/6/docs/api/javax/xml/namespace/NamespaceContext.html">
 * NamespaceContext </a>. Instances are immutable.
 *
 * @author McDowell
 * http://illegalargumentexception.blogspot.nl/2009/05/java-using-xpath-with-namespaces-and.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * An other one here
 * http://ws.apache.org/commons/util/apidocs/org/apache/ws/commons/util/NamespaceContextImpl.html
 */
public final class NamespaceContextMap implements
        NamespaceContext {

    private final Map<String, String> prefixMap;
    private final Map<String, Set<String>> nsMap;

    /**
     * Constructor that takes a map of XML prefix-namespaceURI values. A defensive
     * copy is made of the map. An IllegalArgumentException will be thrown if the
     * map attempts to remap the standard prefixes defined in the NamespaceContext
     * contract.
     *
     * @param prefixMappings a map of prefix:namespaceURI values
     */
    public NamespaceContextMap(
            Map<String, String> prefixMappings) {
        prefixMap = createPrefixMap(prefixMappings);
        nsMap = createNamespaceMap(prefixMap);
    }

    /**
     * Convenience constructor.
     *
     * @param mappingPairs pairs of prefix-namespaceURI values
     */
    public NamespaceContextMap(String... mappingPairs) {
        this(toMap(mappingPairs));
    }

    private static Map<String, String> toMap(
            String... mappingPairs) {
        Map<String, String> prefixMappings = new HashMap<String, String>(
                mappingPairs.length / 2);
        for (int i = 0; i < mappingPairs.length; i++) {
            prefixMappings
                    .put(mappingPairs[i], mappingPairs[++i]);
        }
        return prefixMappings;
    }

    private Map<String, String> createPrefixMap(
            Map<String, String> prefixMappings) {
        Map<String, String> prefixMap = new HashMap<String, String>(
                prefixMappings);
        addConstant(prefixMap, XMLConstants.XML_NS_PREFIX,
                XMLConstants.XML_NS_URI);
        addConstant(prefixMap, XMLConstants.XMLNS_ATTRIBUTE,
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        return Collections.unmodifiableMap(prefixMap);
    }

    private void addConstant(Map<String, String> prefixMap,
                             String prefix, String nsURI) {
        String previous = prefixMap.put(prefix, nsURI);
        if (previous != null && !previous.equals(nsURI)) {
            throw new IllegalArgumentException(prefix + " -> "
                    + previous + "; see NamespaceContext contract");
        }
    }

    private Map<String, Set<String>> createNamespaceMap(
            Map<String, String> prefixMap) {
        Map<String, Set<String>> nsMap = new HashMap<String, Set<String>>();
        for (Map.Entry<String, String> entry : prefixMap
                .entrySet()) {
            String nsURI = entry.getValue();
            Set<String> prefixes = nsMap.get(nsURI);
            if (prefixes == null) {
                prefixes = new HashSet<String>();
                nsMap.put(nsURI, prefixes);
            }
            prefixes.add(entry.getKey());
        }
        for (Map.Entry<String, Set<String>> entry : nsMap
                .entrySet()) {
            Set<String> readOnly = Collections
                    .unmodifiableSet(entry.getValue());
            entry.setValue(readOnly);
        }
        return nsMap;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        checkNotNull(prefix);
        String nsURI = prefixMap.get(prefix);
        return nsURI == null ? XMLConstants.NULL_NS_URI : nsURI;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        checkNotNull(namespaceURI);
        Set<String> set = nsMap.get(namespaceURI);
        return set == null ? null : set.iterator().next();
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        checkNotNull(namespaceURI);
        Set<String> set = nsMap.get(namespaceURI);
        return set.iterator();
    }

    private void checkNotNull(String value) {
        if (value == null) {
            throw new IllegalArgumentException("null");
        }
    }

    /**
     * @return an unmodifiable map of the mappings in the form prefix-namespaceURI
     */
    public Map<String, String> getMap() {
        return prefixMap;
    }


}
