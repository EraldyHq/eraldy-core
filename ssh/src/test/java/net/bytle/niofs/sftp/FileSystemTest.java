
package net.bytle.niofs.sftp;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.*;

import static org.junit.Assert.*;


/**
 * Test the sftp file systems created by:
 * {@link ExternalResourceFileSystemsRule}
 */
@RunWith(value = Parameterized.class)
public class FileSystemTest {

    // The set of test (and/of parameters) is declared below
    // The name argument below will be present in the Junit output
    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<FileSystem> data() {

        return testFileSystem.getIterable();

    }

    @ClassRule
    public static ExternalResourceFileSystemsRule testFileSystem = new ExternalResourceFileSystemsRule();

    private FileSystem fileSystem;

    public FileSystemTest(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Test
    public void sftpFileSystemIsNotNull() throws Exception {

        assertNotNull(fileSystem);

    }

    @Test
    public void sftpFileSystemIsOpen() throws Exception {
        assertEquals("The File System must be opened", true, fileSystem.isOpen());
    }





}
