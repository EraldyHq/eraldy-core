package net.bytle.niofs.sftp;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by gerard on 18-05-2016.
 */
@RunWith(value = Parameterized.class)
public class FileVisitorTest {

    // The set of test (and/of parameters) is declared below
    // The name argument below will be present in the Junit output
    @Parameterized.Parameters(name = "{index}: Resource {0}")
    public static Iterable<FileSystem> data() {

        return testFileSystem.getIterable();

    }

    @ClassRule
    public static ExternalResourceFileSystemsRule testFileSystem = new ExternalResourceFileSystemsRule();

    private FileSystem fileSystem;

    public FileVisitorTest(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }


    @Test
    public void visitFile() throws IOException {

        Path start = fileSystem.getPath(".");
        Files.walkFileTree(start,new FileVisitorSimple(false));

    }

}
