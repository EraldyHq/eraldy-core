package net.bytle.doctest;


import net.bytle.fs.Fs;
import net.bytle.type.Strings;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocTest {

  @Test
  public void baselineTest() {

    // Parsing
    List<DocUnit> docUnits = DocParser.getDocTests("./src/test/resources/docTest/Baseline.txt");
    final int expected = 4;
    Assert.assertEquals(expected + " tests were found", expected, docUnits.size());

    DocExecutor docExecutor = DocExecutor.create("test");
    // A runnner
    DocExecutorUnit docExecutorUnit = DocExecutorUnit.create(docExecutor)
      .addMainClass("echo", CommandEcho.class);

    // First test
    final DocUnit firstDocUnit = docUnits.get(0);
    String testName = "first test";
    Assert.assertEquals(testName + ": Code was found", "System.out.println(\"First test\");", firstDocUnit.getCode().trim());
    Assert.assertEquals(testName + ": Expectation was found", "First test", firstDocUnit.getConsole().trim());
    Assert.assertEquals(testName + ": Expectation and result are the same", firstDocUnit.getConsole().trim(), docExecutorUnit.run(firstDocUnit));
    Assert.assertEquals(testName + ": Language is java", "java", firstDocUnit.getLanguage());
    Assert.assertEquals(testName + " : The first index of the console is correct", Integer.valueOf(136), firstDocUnit.getConsoleLocation()[0]);
    Assert.assertEquals(testName + " : The second index of the console is correct", Integer.valueOf(162), firstDocUnit.getConsoleLocation()[1]);


    final DocUnit secondDocUnit = docUnits.get(1);
    testName = "second test";
    Assert.assertEquals(testName + ": Code was found", "System.out.println(Second test\");", secondDocUnit.getCode().trim());
    Assert.assertNull(testName + ": Expectation is null", secondDocUnit.getConsole());
    boolean error = false;
    try {
      docExecutorUnit.run(secondDocUnit);
    } catch (Exception e) {
      error = true;
    }
    Assert.assertTrue(testName + ": Error when running", error);
    Assert.assertEquals(testName + ": Language is java", "java", secondDocUnit.getLanguage());
    Assert.assertNull(testName + " : The indexes of the expecation is null", secondDocUnit.getConsoleLocation());

    final DocUnit thirdDocUnit = docUnits.get(2);
    testName = "third test";
    Assert.assertEquals(testName + ": Code was found", "System.out.println(\"Third test\");", thirdDocUnit.getCode().trim());
    Assert.assertNull(testName + ": Expectation is null", thirdDocUnit.getConsole());
    Assert.assertNotEquals(testName + ": Expectation and result are not the same", thirdDocUnit.getConsole(), docExecutorUnit.run(thirdDocUnit));
    Assert.assertEquals(testName + ": Language is java", "java", thirdDocUnit.getLanguage());
    Assert.assertNull(testName + " : The indexes of the expecation is null", thirdDocUnit.getConsoleLocation());


    final DocUnit fourthDocUnit = docUnits.get(3);
    testName = "fourth test";

    Assert.assertEquals(testName + ": Code was found", "echo Hello Nico", fourthDocUnit.getCode().trim());
    Assert.assertEquals(testName + ": Expectation is the same", "HelloNico", fourthDocUnit.getConsole().trim());
    Assert.assertEquals(testName + ": Language is dos", "dos", fourthDocUnit.getLanguage());
    Assert.assertEquals(testName + ": Expectation and result are the same", fourthDocUnit.getConsole().trim(), docExecutorUnit.run(fourthDocUnit));
    Assert.assertEquals(testName + " : The first index of the expectation is correct", Integer.valueOf(591), fourthDocUnit.getConsoleLocation()[0]);
    Assert.assertEquals(testName + " : The second index of the expectation is correct", Integer.valueOf(616), fourthDocUnit.getConsoleLocation()[1]);

  }

  /**
   * An normal system exit (ie 0 status) should not:
   * * thrown an error
   * * and exit the process
   * This exist is used when the help is asked to terminate the process in a normal wa
   */
  @Test
  public void noExitTest() {

    final String textToPrint = "Exiting";
    DocUnit docUnit = DocUnit.get()
      .setLanguage("java")
      .setCode("System.out.println(\"" + textToPrint + "\");\n" +
        "System.exit(0);")
      .setConsoleContent(textToPrint);

    DocExecutor docExec = DocExecutor.create("test");
    // A runner
    boolean error = false;
    try {
      DocExecutorUnit.create(docExec)
        .run(docUnit);
    } catch (Exception e) {
      error = true;
    }

    Assert.assertFalse("An error was not thrown and the run has not exited", error);

  }

  /**
   * Replace an expectation by the real result
   */
  @Test
  public void replaceDocTest() throws IOException {


    final Path path = Paths.get("./src/test/resources/docTest/ToBeUpdated.txt");
    List<DocUnit> docUnits = DocParser.getDocTests(path);
    DocUnit docUnit = docUnits.get(0);
    DocExecutor docExecutor = DocExecutor.create("test");
    String result = DocExecutorUnit.create(docExecutor).run(docUnit);
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
    Assert.assertEquals("The run and the expectations are now the same with the file (" + tempFile.toAbsolutePath() + ")", docUnit.getConsole(), result);


  }

  /**
   * When giving a shell code, you may use environment variable
   */
  @Test
  public void envSettingsInDosTest() {

    final Path path = Paths.get("./src/test/resources/docTest/withEnv.txt");
    DocUnit docUnit = DocParser.getDocTests(path).get(0);
    DocExecutor docExecutor = DocExecutor.create("test");
    DocExecutorUnit docExecutorUnit = DocExecutorUnit.create(docExecutor)
      .addMainClass("echo", CommandEcho.class);

    Assert.assertEquals("The run and the expectations are the same ", docUnit.getConsole().trim(), docExecutorUnit.run(docUnit));

  }

  /**
   * Run against a doc without expectation
   */
  @Test
  public void docTestWithoutConsole() {

    List<DocExecutorResult> doc =
      DocExecutor.create("whatever")
        .addCommand("echo", CommandEcho.class)
        .run(Paths.get("./src/test/resources/docTest/withoutExpectation.txt"));

    Assert.assertEquals(1, doc.size());
  }

  @Test
  public void overWriteFileContentTest() throws IOException {

    final Path rootFile = Paths.get("./src/test/resources");
    Path docToRun = Paths.get("./src/test/resources/docTest/fileTest.txt");
    DocExecutorResult docTestRun = DocExecutor.create("defaultRun")
      .addCommand("cat", CommandCat.class)
      .setBaseFileDirectory(rootFile)
      .run(docToRun)
      .get(0);
    Assert.assertEquals("No Errors were seen", 0, docTestRun.getErrors());

    Path newPath = Files.createTempFile("replaceFileContent", "txt");
    String newDoc = docTestRun.getNewDoc();
    Fs.toFile(newDoc, newPath);

    // The new file structure must not be deleted
    List<DocUnit> docUnits = DocParser.getDocTests(newPath);
    Assert.assertEquals("Three unit", 3, docUnits.size());

    // First block
    List<DocFileBlock> fileBlocks = docUnits.get(0).getFileBlocks();
    Assert.assertEquals("One file block", 1, fileBlocks.size());
    DocFileBlock docFileBlock = fileBlocks.get(0);
    String docTestContent = docFileBlock.getContent();
    String fileContent = Strings.createFromPath(Paths.get(rootFile.toString(), "docFile", "file.txt")).toString();
    Assert.assertEquals("Content should be the same", Strings.createFromString(fileContent).normalize().toString(), Strings.createFromString(docTestContent).normalize().toString());

    // Second block
    fileBlocks = docUnits.get(1).getFileBlocks();
    Assert.assertEquals("One file block", 1, fileBlocks.size());
    docFileBlock = fileBlocks.get(0);
    docTestContent = docFileBlock.getContent();
    fileContent = Fs.getFileContent(Paths.get(rootFile.toString(), "docFile", "file.yml"));
    Assert.assertEquals("Content should be the same", Strings.createFromString(fileContent).normalize().toString(), Strings.createFromString(docTestContent).normalize().toString());

    // Third block
    fileBlocks = docUnits.get(2).getFileBlocks();
    Assert.assertEquals("One file block", 1, fileBlocks.size());
    docFileBlock = fileBlocks.get(0);
    docTestContent = docFileBlock.getContent();
    fileContent = Fs.getFileContent(Paths.get(rootFile.toString(), "docFile", "file.yml"));
    Assert.assertEquals("Content should be the same for the third", Strings.createFromString(fileContent).normalize().toString(), Strings.createFromString(docTestContent).normalize().toString());

    // Last code unit section is the same
    String docToRunString = Strings.createFromPath(docToRun).toString();
    String unitClosingTag = "</unit>";
    String lastPart = docToRunString.substring(docToRunString.lastIndexOf(unitClosingTag) + unitClosingTag.length());
    String lastPartNewDoc = newDoc.substring(newDoc.lastIndexOf(unitClosingTag) + unitClosingTag.length());
    Assert.assertEquals("The last part of the doc should be equal", lastPart, lastPartNewDoc);

  }

  /**
   * When the doc terminates with a unit node, no eol should be added
   *
   */
  @Test
  public void overWriteFileContentSecondTest() {

    final Path rootFile = Paths.get("./src/test/resources");
    Path docToRun = Paths.get("./src/test/resources/docTest/overwriteSecond.txt");
    String before = Strings.createFromPath(docToRun).toString();
    DocExecutorResult docTestRun = DocExecutor.create("defaultRun")
      .addCommand("echo", CommandEcho.class)
      .setBaseFileDirectory(rootFile)
      .run(docToRun)
      .get(0);
    Assert.assertEquals("No Errors were seen", 0, docTestRun.getErrors());


    String newDoc = docTestRun.getNewDoc();

    // Last code unit section is the same
    String docToRunString = Strings.createFromPath(docToRun).toString();
    String unitClosingTag = "</unit>";
    String lastPart = docToRunString.substring(docToRunString.lastIndexOf(unitClosingTag) + unitClosingTag.length());
    String lastPartNewDoc = newDoc.substring(newDoc.lastIndexOf(unitClosingTag) + unitClosingTag.length());
    Assert.assertEquals("The last part of the doc should be equal", lastPart, lastPartNewDoc);

    // Same
    String after = Strings.createFromPath(docToRun).toString();
    Assert.assertEquals(before, after);

  }

}
