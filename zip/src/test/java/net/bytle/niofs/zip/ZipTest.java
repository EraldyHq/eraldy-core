package net.bytle.niofs.zip;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.bytle.niofs.zip.Demo.getZipFSProvider;

public class ZipTest {

  @Test
  public void localToZipTest() throws IOException {
    FileSystemProvider provider = getZipFSProvider();

    Map<String, String> env = new HashMap<>();
    env.put("create", "true"); // if the file does not exist, it will be created
    Path zipFile = Paths.get("./build/foo.zip");
    if (Files.exists(zipFile)){
      Files.delete(zipFile);
    }
    // Hierarchical URI
    URI uri = URI.create("zip:"+zipFile.toUri().toString());
    FileSystem fs = provider.newFileSystem(uri, env);
    Path target = fs.getPath("README.md");
    Path source = Paths.get("README.md");
    Files.copy(source,target);

  }

  @Test
  public void httpZipToLocalTest() throws IOException, URISyntaxException {
    FileSystemProvider provider = getZipFSProvider();
    Map<String, String> env = Collections.emptyMap();
    URL zipFile = new URL("https://gerardnico.com/datafile/IpToCountry.zip");
    Path source = Paths.get(zipFile.toURI());
    Path zipTemp = Paths.get("./build/IpToCountry.zip");
    Files.copy(source,zipTemp,StandardCopyOption.REPLACE_EXISTING);
    FileSystem fs = FileSystems.newFileSystem(zipTemp, null);
    Path zipPath = fs.getPath("IpToCountry.csv");
    Path target = Paths.get("./build/IpToCountry.csv");
    Files.copy(zipPath,target,StandardCopyOption.REPLACE_EXISTING);
  }
}
