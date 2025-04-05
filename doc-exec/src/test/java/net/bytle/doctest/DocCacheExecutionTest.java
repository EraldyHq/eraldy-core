package net.bytle.doctest;

import net.bytle.fs.Fs;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Cache test
 */
public class DocCacheExecutionTest {


  /**
   * The cache check first if the md5 of the file has changed
   */
  @Test
  public void fileCacheTest() {
    Path docPath = Paths.get("src/test/resources/docTest/Cache.txt");
    String runName = "defaultRun";
    DocCache docCache = DocCache.get(runName);
    docCache.purgeAll();
    DocExecutorResult result = DocExecutor.create(runName)
      .setCache(docCache)
      .run(docPath)
      .get(0);
    Assert.assertEquals("no error", 0, result.getErrors());
    Assert.assertEquals("The doc has been executed", true, result.hasRun());
    result = DocExecutor.create(runName)
      .setCache(docCache)
      .run(docPath)
      .get(0);
    Assert.assertEquals("The doc has not been executed", false, result.hasRun());
  }

  /**
   * If the file has changed, there is also a code block cache.
   * Only the changed cache are run.
   */
  @Test
  public void codeBlockCacheTest() {
    String firstUnit = "<unit><code dos>echo First</code></unit>";
    String secondUnit = "<unit><code dos>echo Second</code></unit>";
    String thirdUnit = "<unit><code dos>echo Third</code></unit>";
    String doc = firstUnit + "\n" + secondUnit + "\n" + thirdUnit;
    Path docPath = Fs.createTempFileWithContent(doc, ".txt");

    String runName = "cacheDocBlock";
    DocCache docCache = DocCache.get(runName);
    final DocExecutor docExecutor = DocExecutor.create(runName)
      .setCache(docCache)
      .addCommand("echo", CommandEcho.class);

    /**
     * The first run has no cache
     * We just create the cache
     */
    DocExecutorResult result = docExecutor
      .run(docPath)
      .get(0);
    Assert.assertEquals("no error", 0, result.getErrors());
    Assert.assertEquals("The doc has been executed", true, result.hasRun());

    /**
     * Run with cache
     */
    result = docExecutor
      .run(docPath)
      .get(0);
    Assert.assertEquals("The doc has not been executed because the md5 is the same", false, result.hasRun());

    /**
     * If we update only the second unit, only this unit and the other in the series
     * should be executed (not the first one)
     */
    secondUnit = "<unit><code dos>echo Nicolas</code></unit>";
    doc = firstUnit + "\n" + secondUnit + "\n" + thirdUnit;
    Fs.write(docPath, doc);
    result = docExecutor
      .run(docPath)
      .get(0);
    Assert.assertEquals("The doc has been executed", true, result.hasRun());
    Assert.assertEquals("Two code unit has been executed", 2, result.getCodeExecution());
  }

  /**
   * Checking the path where the file will be cached
   */
  @Test
  public void cachePathFileTest() {

    Path path = Paths.get("./hello");
    Path cachedPath = DocCache.get("test").getPathCacheFile(path);
    Path expectedPath = Paths.get(Fs.getUserAppData(DocExecutor.APP_NAME).toString(), "test", "hello");
    Assert.assertEquals("The Path are equals", expectedPath, cachedPath);

  }

  /**
   * Checking a full windows path
   */
  @Test
  public void cacheWindowsPathFileTest() {

    Path path = Paths.get("C:\\hello\\file.txt");
    Path cachedPath = DocCache.get("test").getPathCacheFile(path);
    Path expectedPath = Paths.get(Fs.getUserAppData(DocExecutor.APP_NAME).toString(), "test", "hello", "file.txt");
    Assert.assertEquals("The Path are equals", expectedPath, cachedPath);

  }

  /**
   * Adding a space to the code should lead to a cache miss and run the code again
   */
  @Test
  public void spaceCacheMissTest() {
    final String prefix = "<unit><code dos>echo First";
    final String suffix = "</code></unit>";
    String doc = prefix + suffix;
    Path docPath = Fs.createTempFileWithContent(doc, ".txt");

    String runName = "cacheDocBlock";
    DocCache docCache = DocCache.get(runName);
    final DocExecutor docExecutor = DocExecutor.create(runName)
      .setCache(docCache)
      .addCommand("echo", CommandEcho.class);

    DocExecutorResult result = docExecutor
      .run(docPath)
      .get(0);
    Assert.assertEquals("no error", 0, result.getErrors());
    Assert.assertEquals("The doc has been executed", true, result.hasRun());
    Assert.assertEquals("The code has been executed once", 1, result.getCodeExecution());
    // The code has now a sspace
    doc = prefix + " " + suffix;
    Fs.write(docPath, doc);
    result = docExecutor
      .run(docPath)
      .get(0);
    Assert.assertEquals("The doc with the space has been executed", true, result.hasRun());
    Assert.assertEquals("The code has still been executed once with the space", 1, result.getCodeExecution());
  }

}
