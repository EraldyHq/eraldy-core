package net.bytle.niofs.http;

import net.bytle.fs.Fs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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

    URL website = new URL("https://httpbin.org/html");
    FileSystem fileSystem = FileSystems.newFileSystem(website.toURI(), new HashMap<>());
    Assertions.assertEquals(HttpFileSystem.class, fileSystem.getClass());
    HttpFileSystem httpFileSystem = (HttpFileSystem) fileSystem;
    String workingPath = httpFileSystem.getWorkingStringPath();
    String expectedWorkingPath = "/html";
    Assertions.assertEquals(expectedWorkingPath, workingPath);

  }


  @Test
  public void testGetWithCopyRequest() throws IOException, URISyntaxException {
    URL website = new URL("https://httpbin.org/html");
    Path sourcePath = Paths.get(website.toURI());
    Path targetPath = Paths.get("target/index.html");
    if (Files.exists(targetPath)) {
      Files.delete(targetPath);
    }
    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    long size = Files.size(targetPath);
    Assertions.assertTrue( size > 0,"Target File (" + targetPath + ") has a size (" + size + ") bigger than 0");
    Assertions.assertEquals(3741, size);
  }

  /**
   * {@link Files#readAllBytes(Path)}
   *
   */
  @Test
  public void readAllBytes() throws IOException, URISyntaxException, InterruptedException {

    URL website = new URL("https://httpbin.org/html");
    Path path = Paths.get(website.toURI());

    try (SeekableByteChannel sbc = Files.newByteChannel(path);
         InputStream in = Channels.newInputStream(sbc)) {
      Assertions.assertEquals(HttpSeekableByteChannel.class, sbc.getClass());
      HttpSeekableByteChannel sbcHttp = (HttpSeekableByteChannel) sbc;

      // The size should be known, not -1
      // because it's used by Java to create an array
      // to receive the bytes
      long size = sbcHttp.size();
      Assertions.assertNotEquals(-1, size);

    }

    String content = Fs.getFileContent(path);
    Assertions.assertTrue(content.contains("html"));

  }

  @Test
  public void testSize() throws IOException, URISyntaxException, InterruptedException {
    long expectedBytes = 226L;
    URL website = new URL("https://httpbin.org/range/" + expectedBytes);
    Path sourcePath = Paths.get(website.toURI());
    long size = Files.size(sourcePath);
    Assertions.assertEquals( expectedBytes, size,"Size is good");
  }

  /**
   * Does not exist not yet fully implemented see:
   * See {@link HttpFileSystemProvider#checkAccess(Path, AccessMode...)}
   */
  @Disabled
  @Test
  public void doesNotExistDueTo401() throws MalformedURLException, URISyntaxException {

    URL website = new URL("https://httpbin.org/status/401");
    Path sourcePath = Paths.get(website.toURI());
    boolean condition = Files.notExists(sourcePath);
    Assertions.assertTrue(condition);
    boolean readable = Files.isReadable(sourcePath);
    Assertions.assertFalse(readable);

  }


}
