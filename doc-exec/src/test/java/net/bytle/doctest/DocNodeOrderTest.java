package net.bytle.doctest;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DocNodeOrderTest {

    /**
     * File Node must be before Code
     */
    @Test(expected = RuntimeException.class)
    public void docTestFileNodeAfterCode() {

        final Path path = Paths.get("./src/test/resources/docTest/NodeOrderBadfileNodeAfterCode.txt");
        DocParser.getDocTests(path);

    }

    /**
     * Console after code node
     */
    @Test(expected = RuntimeException.class)
    public void docTestConsoleNodeAfterCode() {

        final Path path = Paths.get("./src/test/resources/docTest/NodeOrderBadfileNodeAfterCode.txt");
        DocParser.getDocTests(path);

    }

    /**
     * Console after code node
     */
    @Test
    public void docTestGoodOrder() {

        final Path path = Paths.get("./src/test/resources/docTest/NodeOrderGood.txt");
        DocParser.getDocTests(path);

    }

}
