package net.bytle.doctest;


import net.bytle.fs.Fs;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocFileBlockTest {

  @Test
  public void baselineTest() throws IOException {

    final Path path = Paths.get("./src/test/resources/docTest/fileTest.txt");
    List<DocUnit> docUnits = DocParser.getDocTests(path);
    DocUnit docUnit = docUnits.get(0);
    List<DocFileBlock> docFileBlocks = docUnit.getFileBlocks();
    Assert.assertEquals("One file was found", 1, docFileBlocks.size());
    Assert.assertEquals("Path is good", "docFile/file.txt", docFileBlocks.get(0).getPath());
    Assert.assertEquals("Language is good", "txt", docFileBlocks.get(0).getLanguage());

    DocExecutor docExecutor = DocExecutor.create("test");
    String result = DocExecutorUnit.create(docExecutor)
      .addMainClass("cat", CommandCat.class)
      .run(docUnit);
    Assert.assertNotEquals("The run and the expectations are not the same", docUnit.getConsole(), result);


    String content = Fs.getFileContent(path);
    String newContent = new StringBuilder()
      .append(content, 0, docUnit.getConsoleLocation()[0])
      .append(result)
      .append(content, docUnit.getConsoleLocation()[1], content.length())
      .toString();

    Path tempFile = Files.createTempFile("doctest", ".txt");
    Fs.toFile(newContent, tempFile);

    docUnits = DocParser.getDocTests(tempFile);
    docUnit = docUnits.get(0);
    Assert.assertEquals("The run and the expectations are now the same with the file (" + tempFile.toAbsolutePath().toString() + ")", docUnit.getConsole(), result);

  }




}
