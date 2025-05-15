package net.uri;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by gerard on 26-05-2016.
 * Test on the JDK URI
 * http://docs.oracle.com/javase/8/docs/api/java/net/URI.html
 */
public class UriTest {


    /**
     * We got the error because the path must be absolute as a scheme is specified.
     *
     * @throws URISyntaxException: Relative path in absolute URI: sftp://hostrelativePath#fragment
     */
    @Test(expected = URISyntaxException.class)
    public void pathMustBeAbsoluteWithScheme() throws URISyntaxException {

        URI pathUri  = new URI("sftp", null, null, -1, "relativePath", null, null);
        // An absolute URI specifies a scheme
        assertEquals("This test must fail because the path is relative and that we have a scheme", "sftp://host:re", pathUri.toString());


    }

    /**
     * We can use a relative path without a scheme :)
     * Ps A relative path is a path that doesn't begin by a root (/ or C:\, ...)
     */
    @Test()
    public void pathCanBeRelativeWithoutScheme() throws URISyntaxException {

        String relativePath = "../relativePath";
        URI pathUri  = new URI(null, null, null, -1, relativePath, null, null);
        // An absolute URI specifies a scheme
        assertEquals("Relative to current directory, point to the parent, without scheme", relativePath, pathUri.toString());

        relativePath = "relativePath";
        pathUri  = new URI(null, null, null, -1, relativePath, null, null);
        // An absolute URI specifies a scheme
        assertEquals("Relative to current directory, point to the working dir, without scheme", relativePath, pathUri.toString());

        relativePath = "./relativePath";
        pathUri  = new URI(null, null, null, -1, relativePath, null, null);
        // An absolute URI specifies a scheme
        assertEquals("Relative to current directory, point to the working dir, without scheme", relativePath, pathUri.toString());

    }

    /**
     * A path with a scheme
     *
     */
    @Test()
    public void pathAbsoluteWithScheme() throws URISyntaxException {

        URI pathUri  = new URI("sftp", null, null, -1, "/relativePath", null, null);
        // An absolute URI specifies a scheme
        assertEquals("This test must fail because the path is relative and that we have a scheme", "sftp:/relativePath", pathUri.toString());


    }

    /**
     * A path with a scheme and a host
     *
     */
    @Test()
    public void pathAbsoluteWithSchemeAndHost() throws URISyntaxException {

        URI pathUri  = new URI("sftp", null, "localhost", -1, "/relativePath", null, null);
        // An absolute URI specifies a scheme
        assertEquals("This test must fail because the path is relative and that we have a scheme", "sftp://localhost/relativePath", pathUri.toString());


    }

  @Test()
  public void equality() throws URISyntaxException {

    URI pathUri  = new URI("sftp", null, "localhost", -1, "/relativePath", null, null);
    URI otherPathUri  = new URI("sftp", null, "localhost", -1, "/relativePath", null, null);
    // An absolute URI specifies a scheme
    assertEquals("They should be equals", pathUri, otherPathUri);


  }

  @Test
  public void dataUriTest() {
    URI uri = URI.create("data-uri:build@project");
    //URI pathUri  = new URI("data-uri", null, null, -1, "relativePath", null, null);
    assertEquals("data-uri",uri.getScheme());
    assertNull(uri.getPath());
    assertNull(uri.getHost());
    assertEquals("build@project",uri.getSchemeSpecificPart());
  }

}
