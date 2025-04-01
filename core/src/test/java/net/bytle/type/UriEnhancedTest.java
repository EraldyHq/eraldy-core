package net.bytle.type;


import net.bytle.exception.IllegalStructure;
import org.junit.Assert;
import org.junit.Test;

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
}
