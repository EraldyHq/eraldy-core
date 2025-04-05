package net.bytle.doctest;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocParserTest {


    /**
     * The console node is not mandatory
     */
    @Test
    public void consoleNotMandatory() {

        final Path path = Paths.get("./src/test/resources/docTest/ParserNoConsole.txt");
        List<DocUnit> docTests = DocParser.getDocTests(path);
        Assert.assertEquals("One unit found", 1, docTests.size());
        Assert.assertNull("Console is null", docTests.get(0).getConsoleLocation());

    }

}
