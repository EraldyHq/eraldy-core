package net.bytle.niofs.http;

import net.bytle.fs.Fs;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.HashMap;


public class HttpFileSystemTest {


  @Test
  public void testNewFileSystem() throws IOException, URISyntaxException {

    URL website = new URL("https://datacadamia.com/start");
    FileSystem fileSystem = FileSystems.newFileSystem(website.toURI(), new HashMap<>());
    Assert.assertEquals(HttpFileSystem.class, fileSystem.getClass());
    HttpFileSystem httpFileSystem = (HttpFileSystem) fileSystem;
    String workingPath = httpFileSystem.getWorkingStringPath();
    String expectedWorkingPath = "/start";
    Assert.assertEquals(expectedWorkingPath, workingPath);

  }


  @Test
  public void testGetWithCopyRequest() throws IOException, URISyntaxException {
    URL website = new URL("https://datacadamia.com/start");
    Path sourcePath = Paths.get(website.toURI());
    Path targetPath = Paths.get("build/index.html");
    if (Files.exists(targetPath)) {
      Files.delete(targetPath);
    }
    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    long size = Files.size(targetPath);
    Assert.assertTrue("Target File (" + targetPath + ") has a size (" + size + ") bigger than 0", size > 0);
  }

  /**
   * {@link Files#readAllBytes(Path)}
   *
   */
  @Test
  public void readAllBytes() throws IOException, URISyntaxException {

    URL website = new URL("https://datacadamia.com/start");
    Path path = Paths.get(website.toURI());

    try (SeekableByteChannel sbc = Files.newByteChannel(path);
         InputStream in = Channels.newInputStream(sbc)) {
      Assert.assertEquals(HttpSeekableByteChannel.class, sbc.getClass());
      HttpSeekableByteChannel sbcHttp = (HttpSeekableByteChannel) sbc;

      // The size should be known, not -1
      // because it's used by Java to create an array
      // to receive the bytes
      long size = sbcHttp.size();
      Assert.assertNotEquals(-1, size);

    }

    String content = Fs.getFileContent(path);
    Assert.assertTrue(content.contains("html"));

  }

  @Test
  public void testSize() throws IOException, URISyntaxException {
    URL website = new URL("https://datacadamia.com/robots.txt");
    Path sourcePath = Paths.get(website.toURI());
    long size = Files.size(sourcePath);
    Assert.assertEquals("Size is not known unfortunately", 1840, size);
  }

  @Test
  public void doesNotExistDueTo401() throws MalformedURLException, URISyntaxException {

    URL website = new URL("https://us20.api.mailchimp.com/3.0/ping");
    Path sourcePath = Paths.get(website.toURI());
    boolean condition = Files.notExists(sourcePath);
    Assert.assertTrue(condition);
    boolean readable = Files.isReadable(sourcePath);
    Assert.assertFalse(readable);

  }


}
