package net.bytle.type;


import net.bytle.exception.IllegalStructure;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class UriEnhancedTest {

  @Test
  public void UriAsAttributeValue() throws IllegalStructure {
    String uriValue = "https://example.com";
    String uriKey = "uri";
    UriEnhanced uriEnhanced = UriEnhanced
      .create()
      .setScheme("https")
      .setHost("example.com")
      .addQueryProperty(uriKey, uriValue);

    Assert.assertEquals("https://example.com?uri=https://example.com", uriEnhanced.toUri().toString());

    String urlString = uriEnhanced.toUrl().toString();
    Assert.assertEquals("https://example.com?uri=https://example.com", urlString);

    String actualUriValue = UriEnhanced.createFromString(urlString)
      .getQueryProperty(uriKey);

    Assert.assertEquals(uriValue, actualUriValue);


  }

  @Test
  public void UriIllegalCharacter() throws IllegalStructure {
    // the symbol | in the query is illegal, need to be encoded
    UriEnhanced uriEnhanced = UriEnhanced
            .createFromString("https://en.wikipedia.org/w/api.php?action=query&titles=SQL&format=json&prop=description|categories");
    Assert.assertEquals("description|categories", uriEnhanced.getQueryProperty("prop"));
  }

  /**
   * No host with a file uri
   */
  @Test
  public void fileURI() throws IllegalStructure {
    // the symbol | in the query is illegal, need to be encoded
    URI uriEnhanced = UriEnhanced
            .createFromString("file:///my/path").toUri();
    Assert.assertEquals("/my/path", uriEnhanced.getPath());
  }
}
