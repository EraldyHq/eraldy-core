package net.bytle.docExec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class DocErrorExecutionTest {

    /**
     * An Error must be thrown
     */
    @Test()
    public void docTestError() {

        Assertions.assertThrows(RuntimeException.class, ()-> DocExecutor.Run(Paths.get("./src/test/resources/docTest/Error.txt"),"cat", DocCommandCat.class));

    }
}
