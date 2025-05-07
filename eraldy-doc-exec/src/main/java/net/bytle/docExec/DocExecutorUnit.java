package net.bytle.docExec;

import net.bytle.log.Log;
import org.zeroturnaround.exec.ProcessExecutor;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

/**
 * Execute a code block found in a doc
 * <p>
 * A {@link DocExecutorUnit} contains the environment variable and function to run a {@link DocUnit}
 */
public class DocExecutorUnit {

  public static final Log LOGGER = DocLog.LOGGER;
  private final DocExecutor docExecutor;

  /**
   * A map to hold the main class of a appHome. See {@link #addCliMainClass(String, Class)}
   */
  private final HashMap<String, Class<?>> cliClass = new HashMap<>();

  /**
   * A map to hold the qualified path of a cli. See {@link #addCliMainClass(String, Class)}
   */
  private final HashMap<String, String> cliPath = new HashMap<>();

  /**
   * The directory where the compile class are saved
   */
  private final Path outputDirClass;

  /**
   * Get a {@link DocExecutorUnit} with the {@link #create(DocExecutor)} function please
   *
   * @param docExecutor - the context object
   */
  private DocExecutorUnit(DocExecutor docExecutor) {

    outputDirClass = Paths.get(System.getProperty("java.io.tmpdir"), "docTestClass").normalize().toAbsolutePath();
    this.docExecutor = docExecutor;
    try {
      Files.createDirectories(outputDirClass);// Safe if the dir already exist
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param docExecutor
   * @return - a docTestRunner that contains the environment variable and function to run a test
   */
  public static DocExecutorUnit create(DocExecutor docExecutor) {
    return new DocExecutorUnit(docExecutor);
  }


  /**
   * Run and evaluate the code in a {@link DocUnit}
   * This function :
   * * wraps the code in a static method,
   * * run it
   * * capture the stdout and stderr
   * * and return it as a string
   *
   * @param docUnit - The docTestUnit to evaluate
   * @return the stdout and stderr in a string
   * @throws RuntimeException - if something is going wrong
   *                          The method {@Link #run} is exception safe and return the error message back
   */
  String eval(DocUnit docUnit) {


    switch (docUnit.getLanguage()) {
      case "java":
        return executeJavaCode(docUnit.getCode());
      case "dos":
      case "bash":
        // A shell code can have several commands statement
        List<String[]> commands = DocShell.parseShellCommand(docUnit, docUnit.getLanguage());
        StringBuilder output = new StringBuilder();
        // For each statement
        for (String[] command : commands) {
          String[] args = command;
          // the executable of the command
          final String exec = args[0];
          Class<?> importClass = this.getMainClass(exec);

          StringBuilder javaCode = new StringBuilder();
          if (importClass != null) {
            args = Arrays.copyOfRange(args, 1, args.length);
            javaCode
              .append(importClass.getName())
              .append(".main(new String[]{\"")
              .append(String.join("\",\"", args))
              .append("\"});\n");
            output.append(executeJavaCode(javaCode.toString()));
          } else {
            // note: We could create the code in one java class
            // but maven make it impossible to load the org.zeroturnaround.exec package
            // ie for instance:
            // mvn test-compile exec:java -Dexec.mainClass="com.tabulify.doc.DocExec" -Dexec.classpathScope="test" -Dexec.args="howto/file/excel"
            // results in
            // Error: package org.zeroturnaround.exec does not exist
            String qualifiedPath = this.toQualifiedPathIfKnown(exec);
            List<String> argsWithQualifiedPath = new ArrayList<>();
            argsWithQualifiedPath.add(qualifiedPath);
            argsWithQualifiedPath.addAll(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
            try {
              ProcessExecutor processExecutor = new ProcessExecutor()
                .command(argsWithQualifiedPath)
                .environment(docUnit.getEnv())
                .readOutput(true);
              if (this.docExecutor.captureStdErr) {
                processExecutor.redirectError(System.out);
              }
              output.append(
                processExecutor
                  .exitValue(0)
                  .execute()
                  .outputUTF8()
              );
            } catch (Exception e) {
              throw new RuntimeException(e);
            }

          }

        }
        return output.toString();
      default:
        throw new RuntimeException("Language (" + docUnit.getLanguage() + " not yet implemented");
    }


  }

  /**
   * @param cliName - the cli name
   * @return the qualified path or the cli name if unknown
   */
  private String toQualifiedPathIfKnown(String cliName) {
    String s = this.cliPath.get(cliName);
    if (s == null) {
      return cliName;
    }
    return s;
  }


  private String executeJavaCode(String javaCode) {

    // Creation of the java source file
    // You could also extend the SimpleJavaFileObject object as shown in the doc.
    // See SimpleJavaFileObject at https://docs.oracle.com/javase/8/docs/api/javax/tools/JavaCompiler.html

    // The class name that will be created
    // The file will have the same name, and we will also use it to put it as temporary directory name
    final String buildClassName = "javademo";
    final String runMethodName = "run";

    try {
      // Code
      String code = "public class " + buildClassName + " {\n" +
        "    public static void " + runMethodName + "() {\n" +
        "       " + javaCode +
        "    }\n" +
        "}";
      DocSource docSource = new DocSource(buildClassName, code);

      // Verification of the presence of the compilation tool archive
      ClassLoader classLoader = DocExecutorUnit.class.getClassLoader();
      final String toolsJarFileName = "tools.jar";
      String javaHome = System.getProperty("java.home");
      Path toolsJarFilePath = Paths.get(javaHome, "lib", toolsJarFileName);
      if (!Files.exists(toolsJarFilePath)) {
        LOGGER.fine("The tools jar file (" + toolsJarFileName + ") could not be found at (" + toolsJarFilePath + ") but it may still work.");
      }

      // The compile part
      // Get the compiler
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      if (compiler == null) {

        final String message = "Unable to get the system Java Compiler. Are your running java with a JDK ?";
        LOGGER.severe(message);
        LOGGER.severe("Java Home: " + javaHome);
        throw new RuntimeException(message);

      }

      // Create a compilation unit (files)
      Iterable<? extends JavaFileObject> compilationUnits = Collections.singletonList(docSource);
      // A feedback object (diagnostic) to get errors
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

      // Javac options here
      List<String> options = new ArrayList<>();
      options.add("-d");
      options.add(outputDirClass.toString());

      // Add class path to get org.zeroturnaround.exec
      String currentClassPath = System.getProperty("java.class.path");
      LOGGER.fine("Using classpath: " + currentClassPath);
      if (currentClassPath != null && !currentClassPath.isEmpty()) {
        options.add("-classpath");
        options.add(currentClassPath);
      }

      StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

      // Compilation unit can be created and called only once
      JavaCompiler.CompilationTask task = compiler.getTask(
        null,
        fileManager,
        diagnostics,
        options,
        null,
        compilationUnits
      );
      // The compile task is called
      task.call();
      // Printing of any compile problems
      for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {

        final String msg = "Compile Error on line " +
          diagnostic.getLineNumber() +
          " source " +
          diagnostic.getSource() +
          "\nError: " +
          diagnostic.getMessage(null);
        LOGGER.fine(msg);

        throw new RuntimeException(msg + "\nCode:\n" + code);

      }


      // Now that the class was created, we will load it and run it
      LOGGER.fine("Trying to load from " + outputDirClass);
      Class<?> buildClass;
      try (URLClassLoader urlClassLoader = new URLClassLoader(
        new URL[]{outputDirClass.toUri().toURL()},
        classLoader)) {
        // Loading the dynamically build class
        buildClass = urlClassLoader.loadClass(buildClassName);
      }
      Method method = buildClass.getMethod(runMethodName);


      // Capturing outputStream and running the command
      PrintStream backupSystemOut = System.out;
      PrintStream backupSystemErr = System.err;
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      PrintStream stream = new PrintStream(byteArrayOutputStream);
      System.setOut(stream);
      if (this.docExecutor.captureStdErr) {
        System.setErr(stream);
      }
      // Invoke
      try {
        method.invoke(null);
      } catch (InvocationTargetException e) {
        /**
         * is it a {@link DocExitStatusException} thrown by {@link DocSecurityManager}
         */
        if (!e.getTargetException().getClass().equals(DocExitStatusException.class)) {
          // if it's not, throw
          throw new RuntimeException(e.getCause());
        }
        DocExitStatusException exitStatusException = (DocExitStatusException) e.getTargetException();
        if (exitStatusException.getExitStatus() != 0) {
          // Error
          System.out.flush(); // Into the byteArray
          System.err.flush(); // Into the byteArray
          String consoleOutput;
          if(byteArrayOutputStream.size()==0){
            consoleOutput="No output was received";
          } else {
            consoleOutput = byteArrayOutputStream.toString();
          }
          throw new RuntimeException("Error has been seen.\nCode:\n" + javaCode + "Console Output: \n" + consoleOutput, e);
        }
        DocLog.LOGGER.info("Code execution with System exit with 0 has been prevented");

      } finally {

        // Reset the log level
        DocLog.LOGGER.setLevel(Level.INFO);
        // Get the output
        System.out.flush(); // Into the byteArray
        System.err.flush(); // Into the byteArray
        System.setOut(backupSystemOut);
        System.setErr(backupSystemErr);

      }

      return byteArrayOutputStream.toString();

    } catch (NoSuchMethodException | IOException | IllegalAccessException | ClassNotFoundException e) {

      throw new RuntimeException(e);

    }

  }


  /**
   * Call the function {@link #eval(DocUnit)} but is safe of exception
   * It returns the error message if an error occurs
   * The string is also trimmed to suppress the newline and other characters
   *
   * @param docUnit - The docTestUnit to run
   * @return the code evaluated or the message error trimmed
   */
  public String run(DocUnit docUnit) {


    return eval(docUnit).trim();


  }

  /**
   * If the {@link DocUnit#getLanguage() language} is dos or bash,
   * * the first name that we called cli is replaced by the mainClass
   * * the others args forms the args that are passed to the main method of the mainClass
   *
   * @param cli       - the cli name (ie the first word in a shell command)
   * @param mainClass - the main class that implements this appHome
   * @return - a docTestRunner for chaining construction
   */
  public DocExecutorUnit addCliMainClass(String cli, Class<?> mainClass) {

    this.cliClass.put(cli, mainClass);
    return this;
  }

  /**
   * @param cli  - the cli name (ie the first word in a shell command)
   * @param path - the fully qualified cli path
   * @return - a docTestRunner for chaining construction
   */
  public DocExecutorUnit addCliPath(String cli, String path) {

    this.cliPath.put(cli, path);
    return this;
  }

  /**
   * @param cli - the cli/exec
   * @return the main class that implements a cli/exec
   * <p>
   * This is used to generate Java code when the documentation is a shell documentation
   */
  public Class<?> getMainClass(String cli) {
    return this.cliClass.get(cli);
  }

}
