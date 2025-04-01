package net.bytle.fs;


import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FsTest {

  @Test
  public void baseGetDirectoryNamesInBetweenTest() {
    Path basePath = Paths.get("tmp");
    Path path = Paths.get("tmp", "foo", "bar", "hello");

    List<String> names = Fs.getDirectoryNamesInBetween(path, basePath);
    String[] strings = {"foo", "bar"};
    Assert.assertEquals("There is two names", Arrays.asList(strings), names);
  }

  /**
   * basePath does't share the same root with path
   */
  @Test(expected = RuntimeException.class)
  public void failGetDirectoryNamesInBetweenTest() {

    Path basePath = Paths.get("tmp");
    Path path = Paths.get("foo", "bar", "hello");

    Fs.getDirectoryNamesInBetween(path, basePath);

  }

  /**
   * basePath should be path returns nothing
   */
  @Test(expected = RuntimeException.class)
  public void fail2GetDirectoryNamesInBetweenTest() {

    Path basePath = Paths.get("tmp");
    Path path = Paths.get("tmp", "foo", "bar", "hello");

    Fs.getDirectoryNamesInBetween(basePath, path);

  }

  @Test
  public void getPathUntilNameTest() {

    String nameToFound = "bar";
    Path pathToFound = Paths.get("foo").resolve(nameToFound);
    Path leafPath = pathToFound.resolve("ni").resolve("co");

    Path foundPath = Fs.getPathUntilName(leafPath, nameToFound);
    Assert.assertNotNull("The path should have been found ", foundPath);
    Assert.assertEquals("The paths should be the same", pathToFound, foundPath);

    foundPath = Fs.getPathUntilName(leafPath.toAbsolutePath(), nameToFound);
    Assert.assertNotNull("The absolute path should have been found ", foundPath);
    Assert.assertEquals("The absolute paths should be the same", pathToFound.toAbsolutePath(), foundPath);

  }

  @Test
  public void fsCloserTest() throws FileNotFoundException {

    Path tempDirectory = Fs.getTempDirectory();
    String name = "closer.txt";
    Path closerFileToFind = tempDirectory.resolve(name);
    Fs.write(closerFileToFind, "content");
    Path fileContext = tempDirectory.resolve("directory").resolve("long").resolve("inthedirectory");

    Path closest = Fs.closest(fileContext, name);
    Assert.assertEquals(closerFileToFind.toAbsolutePath(), closest);

  }

  @Test
  public void setAttribute() throws IOException {
    Path path = Paths.get("src", "test", "resources", "fs", "set-attribute.txt");

    Fs.setUserAttribute(path, "key", "value");

  }

}
