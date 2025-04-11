package net.bytle.docExec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Dos text
 */
public class DocShellTest {


    @Test
    public void baselineTest() {
        final Path path = Paths.get("./src/test/resources/docTest/dos.txt");
        List<DocUnit> docUnits = DocParser.getDocTests(path);
        DocUnit docUnit = docUnits.get(0);
        DocExecutor docExecutor = DocExecutor.create("test");
        String result = DocExecutorUnit.create(docExecutor)
                .addMainClass("echo", DocCommandEcho.class)
                .run(docUnit);
        Assertions.assertEquals(docUnit.getConsole().trim(), result, "The run and the expectations are the same");
    }

    @Test
    public void parsingDos() {
        String code = "echo Foo\r\n" +
                ":: Comments\r\n" +
                "echo Yolo";
        DocUnit docUnit = DocUnit.get().setCode(code);
        List<String[]> commands = DocShell.parseShellCommand(docUnit, DocShell.DOS_LANG);
        Assertions.assertEquals(2, commands.size(), "There is only two commands in this code");
        String[] command1 = {"echo", "Foo"};
        Assertions.assertEquals(String.join(",", command1), String.join(",", commands.get(0)), "The first command");
    }

    @Test
    public void parsingBash() {
        String code = "echo Foo\n" +
                "# Comments\n" +
                "echo Yolo";
        DocUnit docUnit = DocUnit.get().setCode(code);
        List<String[]> commands = DocShell.parseShellCommand(docUnit, DocShell.BASH_LANG);
        Assertions.assertEquals(2, commands.size(), "There is only two commands in this code");
        String[] command1 = {"echo", "Foo"};
        Assertions.assertEquals(String.join(",", command1), String.join(",", commands.get(0)), "The first command");
    }

    @Test
    public void envWindowsTest() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (!isWindows) {
            return;
        }
        String code = "echo %USERPROFILE%";
        DocUnit docUnit = DocUnit.get().setCode(code);
        List<String[]> commands = DocShell.parseShellCommand(docUnit, DocShell.DOS_LANG);
        final String userProfileExpansion = commands.get(0)[1];
        Assertions.assertTrue(userProfileExpansion.contains("Users"), "contains the user directory (ie C:\\Users\\name)");
    }

    @Test
    public void envBashTest() {
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().contains("linux")) {
            return;
        }
        String code = "echo $HOME";
        DocUnit docUnit = DocUnit.get().setCode(code);
        List<String[]> commands = DocShell.parseShellCommand(docUnit, DocShell.BASH_LANG);
        final String userProfileExpansion = commands.get(0)[1];
        Assertions.assertEquals(System.getenv().get("HOME"), userProfileExpansion, "contains the user directory");

    }
}
