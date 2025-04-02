package net.bytle.niofs.http;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;


public class HttpRequestPathTest {

  @Test
  public void getParent() throws MalformedURLException, URISyntaxException {
    URL website = new URL("https://example.com/foo/bar");
    Path sourcePath = Paths.get(website.toURI());
    Assert.assertEquals("File name", "bar", sourcePath.getFileName().toString());
    Assert.assertTrue("Path is absolute", sourcePath.isAbsolute());

    // parent
    Path parent = sourcePath.getParent();
    Assert.assertEquals("Parent name", "/foo", parent.toString());
    Assert.assertEquals("Parent name", "foo", parent.getFileName().toString());
    Assert.assertEquals("Parent name", "https://example.com/foo", parent.toUri().toString());


  }

  @Test
  public void getSibling() throws MalformedURLException, URISyntaxException {
    URL website = new URL("https://example.com/foo/bar");
    Path sourcePath = Paths.get(website.toURI());
    Assert.assertEquals("File name", "bar", sourcePath.getFileName().toString());
    Assert.assertTrue("Path is absolute", sourcePath.isAbsolute());

    // parent
    Path parent = sourcePath.getParent();
    Assert.assertEquals("Parent name", "/foo", parent.toString());
    Assert.assertEquals("Parent name", "foo", parent.getFileName().toString());
    Assert.assertEquals("Parent name", "https://example.com/foo", parent.toUri().toString());

    // sibling
    Path sibling = sourcePath.resolveSibling("sibling");
    Assert.assertEquals("Sibling path", "/foo/sibling", sibling.toString());
    Assert.assertEquals("Sibling name", "sibling", sibling.getFileName().toString());
    Assert.assertEquals("Sibling URI name", "https://example.com/foo/sibling", sibling.toUri().toString());
    Assert.assertEquals("Sibling Root", "/", sibling.getRoot().toString());

  }

  @Test
  public void getParentNull() throws MalformedURLException, URISyntaxException {
    URL website = new URL("https://example.com/");
    Path sourcePath = Paths.get(website.toURI());

    Assert.assertEquals(sourcePath.toString(), "/");

    // the spec say to it must be null
    Path parent = sourcePath.getParent();
    Assert.assertNull(parent);
  }

  @Test
  public void getHttpSystemFromUriWithoutPath() throws MalformedURLException, URISyntaxException {

    URL website = new URL("https://example.com");
    Path path = Paths.get(website.toURI());

    Assert.assertEquals("", path.toString());
    Assert.assertFalse(path.isAbsolute());
    Assert.assertEquals("https://example.com", path.toUri().toString());

    path = path.resolve("yolo");
    Assert.assertFalse(path.isAbsolute());
    path = path.toAbsolutePath();
    Assert.assertEquals("/yolo", path.toString());

    Assert.assertEquals("https://example.com/yolo", path.toUri().toString());

  }

  @Test
  public void attributeTest() throws IOException, URISyntaxException {

    URL website = new URL("https://example.com/foo/bar");
    Path sourcePath = Paths.get(website.toURI());

    final String myAttrValue = "Mémé dans les orties";
    String attribute = "user:tags";
    Files.setAttribute(sourcePath, attribute, myAttrValue);
    Object value = Files.getAttribute(sourcePath, attribute);
    Assert.assertNotNull(value);
    Assert.assertEquals(myAttrValue, value.toString());

  }

  @Test
  public void testAbsolutePath() {

    String urlString = "https://datacadamia.com";
    URI website = URI.create(urlString);
    HttpFileSystemProvider provider = new HttpFileSystemProvider();
    HttpFileSystem httpFileSystem = provider.newFileSystem(website, new HashMap<>());

    Path path = httpFileSystem.getPath("/yolo");
    Assert.assertTrue(path.isAbsolute());

    path = httpFileSystem.getPath("/yolo", "yolo");
    Assert.assertTrue(path.isAbsolute());
    Assert.assertEquals("/yolo/yolo", path.toString());


  }

  @Test
  public void relativePath() throws IOException, URISyntaxException {

    URL website = new URL("https://datacadamia.com/start");
    HttpFileSystem fileSystem = (HttpFileSystem) FileSystems.newFileSystem(website.toURI(), new HashMap<>());

    Path path = fileSystem.getPath("yolo");
    Assert.assertFalse(path.isAbsolute());

    path = path.toAbsolutePath();
    Assert.assertEquals("/start/yolo", path.toString());

    Path parent = path.getParent();
    Assert.assertEquals("/start", parent.toString());

    Assert.assertEquals(fileSystem.getWorkingStringPath(), parent.toString());

    path = fileSystem.getPath("foo", "bar");
    Assert.assertFalse(path.isAbsolute());
    Assert.assertEquals("foo/bar", path.toString());

    path = fileSystem.getPath("/foo/bar");
    Path root = path.getRoot();
    Assert.assertNotNull(root);
    Path relativePathFromRoot = root.relativize(path);
    Assert.assertEquals("foo/bar", relativePathFromRoot.toString());


  }
}
