package net.bytle.docExec;


import net.bytle.fs.Fs;
import net.bytle.log.Log;
import net.bytle.log.LogLevel;
import net.bytle.log.Logs;
import net.bytle.type.Strings;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


@SuppressWarnings("unused")
public class DocExecutor {


  public static final String APP_NAME = DocExecutor.class.getSimpleName();
  private final String name;

  private final String eol = Strings.EOL;
  private final DocSecurityManager securityManager;
  public boolean captureStdErr = true;

  DocCache docCache;
  private final Map<String, Class<?>> shellCommandMainClassMap = new HashMap<>();
  // The fully qualified path of the command
  // to be sure that we don't hit another command
  private final Map<String, Path> shellCommandAbsolutePathMap = new HashMap<>();
  private final Map<String, Boolean> shellCommandUseShellBinaryMap = new HashMap<>();
  private Level logLevel = LogLevel.INFO;

  /**
   * @param overwrite If set to true, the console and the file node will be overwritten
   * @return the object for chaining
   */
  public DocExecutor setOverwrite(boolean overwrite) {
    this.overwrite = overwrite;
    return this;
  }

  /**
   * @param captureStdErr If set to true, the std err is added to the output
   * @return the object for chaining
   */
  public DocExecutor setCaptureStdErr(boolean captureStdErr) {
    this.captureStdErr = captureStdErr;
    return this;
  }

  private boolean overwrite = false;


  /**
   * @param name The execution name
   */
  private DocExecutor(String name) {
    this.name = name;
    // Managing System.exit in code execution with the security manager
    securityManager = DocSecurityManager.create();
    System.setSecurityManager(securityManager);
  }

  public static List<DocExecutorResult> Run(Path path, String command, Class<?> commandClass) {
    return create("defaultRun").setShellCommandExecuteViaMainClass(command, commandClass).run(path);
  }


  /**
   * @param name - The name of the run (used in the console)
   * @return the object for chaining
   */
  public static DocExecutor create(String name) {

    return new DocExecutor(name);
  }

  /**
   * If a docCache is passed, it will be used
   *
   * @param docCache - A doc cache for this run
   * @return a {@link DocExecutor} for chaining
   */
  public DocExecutor setCache(DocCache docCache) {
    this.docCache = docCache;
    return this;
  }

  /**
   * Execute doc test file and the child of directory defined by the paths parameter
   *
   * @param paths the files to execute
   * @return the list of results
   */
  public List<DocExecutorResult> run(Path... paths) {

    Logs.setLevel(this.logLevel);

    List<DocExecutorResult> results = new ArrayList<>();
    for (Path path : paths) {

      if (!Files.exists(path)) {
        String msg = "The path (" + path.toAbsolutePath() + ") does not exist";
        DocLog.LOGGER.severe(this.name, msg);
        throw new RuntimeException(msg);
      }

      List<Path> childPaths = Fs.getDescendantFiles(path);


      for (Path childPath : childPaths) {

        /**
         * Cache ?
         */
        if (docCache != null) {
          String md5Cache = docCache.getMd5(childPath);
          String md5 = Fs.getMd5(childPath);
          if (md5.equals(md5Cache)) {
            DocLog.LOGGER.info(this.name, "Cache is on and the file (" + childPath + ") has already been executed. Skipping the execution");
            DocExecutorResult docExecutorResult = DocExecutorResult.get(childPath);
            results.add(docExecutorResult);
            continue;
          }
        }

        /**
         * Execution
         */
        DocLog.LOGGER.info(this.name, "Executing the doc file (" + childPath + ")");
        DocExecutorResult docExecutorResult = null;
        try {
          docExecutorResult = this.execute(childPath);
        } catch (NoSuchFileException e) {
          throw new RuntimeException(e);
        }
        results.add(docExecutorResult);
        if (overwrite) {
          // Overwrite the new doc
          Fs.toFile(docExecutorResult.getNewDoc(), childPath);
        }

        if (docCache != null) {
          docCache.store(childPath);
        }

      }
    }
    return results;

  }

  private Path baseFileDirectory = Paths.get(".");

  /**
   * Do we stop at the first error
   */
  private boolean stopRunAtFirstError = true;

  public DocExecutor setStopRunAtFirstError(boolean stopRunAtFirstError) {
    this.stopRunAtFirstError = stopRunAtFirstError;
    return this;
  }


  /**
   * @param path the doc to execute
   * @return the new page
   */
  private DocExecutorResult execute(Path path) throws NoSuchFileException {


    DocExecutorResult docExecutorResult = DocExecutorResult
      .get(path)
      .setHasBeenExecuted(true);

    // Parsing
    List<DocUnit> docTests = DocParser.getDocTests(path);
    String originalDoc = Fs.getFileContent(path);
    StringBuilder targetDoc = new StringBuilder();

    // A code executor
    DocExecutorUnit docExecutorUnit = DocExecutorUnit.create(this);

    List<DocUnit> cachedDocUnits = new ArrayList<>();
    if (docCache != null) {
      cachedDocUnits = docCache.getDocTestUnits(path);
    }
    Integer previousEnd = 0;
    boolean oneCodeBlockHasAlreadyRun = false;
    for (int i = 0; i < docTests.size(); i++) {

      DocUnit docUnit = docTests.get(i);
      DocUnit cachedDocUnit = null;
      if (cachedDocUnits != null && i < cachedDocUnits.size()) {
        cachedDocUnit = cachedDocUnits.get(i);
      }
      // Boolean to decide if we need to execute
      boolean codeChange = false;
      boolean fileChange = false;
      // ############################################
      // The order of execution is important here to reconstruct the new document
      //    * First the processing of the file nodes
      //    * then the code
      //    * then the console
      // ############################################

      // Replace file node with the file content on the file system
      final List<DocFileBlock> files = docUnit.getFileBlocks();
      if (!files.isEmpty()) {

        for (int j = 0; j < files.size(); j++) {

          DocFileBlock docFileBlock = files.get(j);

          final String fileStringPath = docFileBlock.getPath();
          if (fileStringPath == null) {
            throw new RuntimeException("The file path for this unit is null (<file type file.extension>");
          }
          // No need of cache test here because it's going very quick
          if (cachedDocUnit != null) {
            List<DocFileBlock> fileBlocks = cachedDocUnit.getFileBlocks();
            if (fileBlocks.size() > j) {
              DocFileBlock cachedDocFileBlock = fileBlocks.get(j);
              if (!(fileStringPath.equals(cachedDocFileBlock.getPath()))) {
                fileChange = true;
              }
            }
          } else {
            fileChange = true;
          }

          Path filePath = Paths.get(baseFileDirectory.toString(), fileStringPath);
          String fileContent = Strings.createFromPath(filePath).toString();

          int start = docFileBlock.getLocationStart();
          targetDoc.append(originalDoc, previousEnd, start);

          DocLog.LOGGER.info(this.name, "Replacing the file block (" + Log.onOneLine(docFileBlock.getPath()) + ") from the file (" + docUnit.getPath() + ")");
          targetDoc
            .append(eol)
            .append(fileContent)
            .append(eol);

          previousEnd = docFileBlock.getLocationEnd();


        }
      }

      // ######################## Code Block Processing #####################
      // Code block is not mandatory, you may just have a file
      String code = docUnit.getCode();
      if (code != null && !code.trim().isEmpty()) {
        // Check if this unit has already been executed and that the code has not changed
        if (cachedDocUnit != null) {
          if (!(code.equals(cachedDocUnit.getCode()))) {
            codeChange = true;
          }
        } else {
          codeChange = true;
        }

        // Run
        String result;
        if (
          ((codeChange || fileChange) & cacheIsOn())
            || (!cacheIsOn())
            || oneCodeBlockHasAlreadyRun
        ) {
          DocLog.LOGGER.info(this.name, "Running the code (" + Log.onOneLine(code) + ") from the file (" + docUnit.getPath() + ")");
          try {
            docExecutorResult.incrementCodeExecutionCounter();
            result = docExecutorUnit.run(docUnit).trim();
            DocLog.LOGGER.fine(this.name, "Code executed, no error");
            oneCodeBlockHasAlreadyRun = true;
          } catch (Exception e) {
            docExecutorResult.addError();
            if (e.getClass().equals(NullPointerException.class)) {
              result = "null pointer exception";
            } else {
              result = e.getMessage();
            }
            DocLog.LOGGER.severe(this.name, "Error during execute: " + result);
            if (stopRunAtFirstError) {
              DocLog.LOGGER.fine(this.name, "Stop at first run. Throwing the error");
              throw new RuntimeException(e.getMessage(), e);
            }
          }
        } else {
          DocLog.LOGGER.info(this.name, "The run of the code (" + Log.onOneLine(code) + ") was skipped due to caching from the file (" + docUnit.getPath() + ")");
          result = cachedDocUnit.getConsole();
        }

        // Console
        DocBlockLocation consoleLocation = docUnit.getConsoleLocation();
        if (consoleLocation != null) {
          int start = consoleLocation.getStart();
          targetDoc.append(originalDoc, previousEnd, start);
          String console = docUnit.getConsole();
          if (console == null) {
            throw new RuntimeException("No console were found, try a run without cache");
          }
          if (!result.equals(console.trim())) {

            targetDoc
              .append(eol)
              .append(result)
              .append(eol);

            previousEnd = consoleLocation.getEnd();

          } else {

            previousEnd = consoleLocation.getStart();

          }
        }
      }
    }
    targetDoc.append(originalDoc, previousEnd, originalDoc.length());
    docExecutorResult.setNewDoc(targetDoc.toString());
    return docExecutorResult;

  }


  /**
   * Execute a shell command via a Java Main Class
   * <p></p>
   * If the {@link DocUnit#getLanguage() language} is a shell language (dos or bash),
   * * the first name that we called cli is replaced by the mainClass
   * * the others args forms the args that are passed to the main method of the mainClass
   *
   * @param command   - the name of the command (ie the first word in a command statement)
   * @param mainClazz - a main class that will receive the parsed arguments
   * @throws IllegalArgumentException - if the command was already set to use {@link #setShellCommandExecuteViaShellBinary(String, Boolean)}
   */
  public DocExecutor setShellCommandExecuteViaMainClass(String command, Class<?> mainClazz) {
    shellCommandMainClassMap.put(command, mainClazz);
    if (shellCommandUseShellBinaryMap.get(command) == null) {
      shellCommandUseShellBinaryMap.put(command, false);
    } else {
      throw new IllegalArgumentException("The command " + command + " was already set to use the shell binary (bash -c) for execution. You can't have both the main class (" + mainClazz + ") and the shell binary execution");
    }
    return this;
  }

  /**
   * If the {@link DocUnit#getLanguage() language} is a shell language (dos or bash), set the full qualified path of the command.
   * When your command is not in the path and that you can't change it easily,
   * if a path is set, we will replace the command by its full qualified path
   *
   * @param command      - the name of the command (ie the first word in a command statement)
   * @param absolutePath - the qualified path of the command (not the directory, the binary file)
   * @throws IllegalArgumentException - if the path is not absolute
   */
  public DocExecutor setShellCommandQualifiedPath(String command, Path absolutePath) {
    if (!absolutePath.isAbsolute()) {
      throw new IllegalArgumentException("The path (" + absolutePath + ") is not absolute");
    }
    shellCommandAbsolutePathMap.put(command, absolutePath);
    return this;
  }

  /**
   * @param path the base path (Where do we will find the files defined in the file node)
   * @return the runner for chaining instantiation
   */
  public DocExecutor setBaseFileDirectory(Path path) {
    this.baseFileDirectory = path;
    return this;
  }

  public DocExecutor setLogLevel(Level level) {
    this.logLevel = level;
    return this;
  }

  /**
   * @return if the cache is on
   */
  private Boolean cacheIsOn() {
    return docCache != null;
  }

  /**
   * Add a java system property
   *
   * @param key   the key
   * @param value the value
   * @return the object for chaining
   */
  public DocExecutor setSystemProperty(String key, String value) {
    System.setProperty(key, value);
    return this;
  }

  public boolean doesStopAtFirstError() {
    return this.stopRunAtFirstError;
  }

  /**
   * If the {@link DocUnit#getLanguage() language} is a shell language (dos, bash, ...)
   * Use the shell cli or parse the args and execute via java exec
   * (ie use "bash -c" or execute the arguments)
   * It can be handy in environment where no bash is provided
   * <p></p>
   * Note that this parameter has no effect if a {@link #setShellCommandExecuteViaMainClass(String, Class)}
   * command class was specified
   *
   * @param commandName - the command name (the first word in the command statement)
   * @param useShellCli - use "bash -c" to execute the command (True by default) or parse the args and execute via java exec
   * @return the object for chaining
   */
  public DocExecutor setShellCommandExecuteViaShellBinary(String commandName, Boolean useShellCli) {
    this.shellCommandUseShellBinaryMap.put(commandName, useShellCli);
    Class<?> mainClazz = shellCommandMainClassMap.get(commandName);
    if (mainClazz != null) {
      throw new IllegalArgumentException("The command " + commandName + " was already set to use the main class (" + mainClazz + ") for execution. You can't have both the main class (" + mainClazz + ") and the shell binary execution");
    }
    return this;
  }

  /**
   * @param commandName - the command name (the first word in the command statement)
   * @return the Path of the command on the system
   */
  protected Path getShellCommandPath(String commandName) {
    return this.shellCommandAbsolutePathMap.get(commandName);
  }

  /**
   * @param commandName - the cli/exec
   * @return the main class that implements a cli/exec
   * <p>
   * This is used to generate Java code when the documentation is a shell documentation
   */
  public Class<?> getShellCommandMainClass(String commandName) {
    return this.shellCommandMainClassMap.get(commandName);
  }

  protected boolean isExecuteShellCommandViaShellBinary(String commandName) {
    Boolean executeViaShellBinary = this.shellCommandUseShellBinaryMap.get(commandName);
    if (executeViaShellBinary == null) {
      return true;
    }
    return executeViaShellBinary;
  }

  protected DocSecurityManager getSecurityManager() {
    return this.securityManager;
  }



}
