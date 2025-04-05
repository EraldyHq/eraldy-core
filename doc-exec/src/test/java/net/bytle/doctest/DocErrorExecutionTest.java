package net.bytle.doctest;

import org.junit.Test;

import java.nio.file.Paths;

public class DocErrorExecutionTest {

    /**
     * An Error must be thrown
     */
    @Test(expected = RuntimeException.class)
    public void docTestError() {

        DocExecutor.Run(Paths.get("./src/test/resources/docTest/Error.txt"),"cat",CommandCat.class);

    }
}
