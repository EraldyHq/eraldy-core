package net.bytle.doctest;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Dos text
 */
public class DocDosCodeTest {


    @Test
    public void baselineTest() {
        final Path path = Paths.get("./src/test/resources/docTest/dos.txt");
        List<DocUnit> docUnits = DocParser.getDocTests(path);
        DocUnit docUnit = docUnits.get(0);
      DocExecutor docExecutor = DocExecutor.create("test");
        String result = DocExecutorUnit.create(docExecutor)
                .addMainClass("echo", CommandEcho.class)
                .run(docUnit);
        Assert.assertEquals("The run and the expectations are the same", docUnit.getConsole().trim(), result);
    }

    @Test
    public void parsing() {
        String code = "echo Nico\n"+
                ":: Comments\r\n"+
                "echo Yolo";
        DocUnit docUnit = DocUnit.get().setCode(code);
        List<String[]> commands = DocDos.parseDosCommand(docUnit);
        Assert.assertEquals("There is only two commands in this code",2,commands.size());
        String[] command1 = {"echo","Nico"};
        Assert.assertEquals("The first command",String.join(",",command1),String.join(",",commands.get(0)));
    }

    @Test
    public void envTest() {
        String code = "echo %USERPROFILE%";
        DocUnit docUnit = DocUnit.get().setCode(code);
        List<String[]> commands = DocDos.parseDosCommand(docUnit);
        final String userProfileExpansion = commands.get(0)[1];
        Assert.assertTrue("contains the user directory (ie C:\\Users\\name)",userProfileExpansion.contains("Users"));
    }
}
