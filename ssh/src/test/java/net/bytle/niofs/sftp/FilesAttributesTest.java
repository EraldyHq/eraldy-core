package net.bytle.niofs.sftp;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by gerard on 23-05-2016.
 * Test the attributes function of the {@link Files} class.
 * The other functions are tested in the class {@link FilesTest}
 */
@RunWith(value = Parameterized.class)
public class FilesAttributesTest {

    // The set of test (and/of parameters) is declared below
    // The name argument below will be present in the Junit output
    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<FileSystem> data() {

        return testFileSystem.getIterable();

    }

    @ClassRule
    public static ExternalResourceFileSystemsRule testFileSystem = new ExternalResourceFileSystemsRule();

    private FileSystem fileSystem;

    public FilesAttributesTest(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Test
    public void posixFileAttribute() throws IOException {


        Path file = fileSystem.getPath(FilesUtil.TARGET_BASE_DIR_RUN, FilesUtil.TARGET_BASE_DIR_TEST, "FilesAttributes", "FilesAttributesTest.md");
        Path src = Paths.get("src","test", "resources","sftp", "FilesAttributesTest.md");
        FilesUtil.copyAlways(src,file);


        // Modified Time
        // The date time of the file can be different between the local modification and the git commit :(
        // And then the local test may succeed while the continuous automation test may failed
        // Example:
        // Local: 2016-05-02T14:53:27Z
        // Server (Travis): 2016-05-02T14:56:10Z
        // Therefore we set it first
        String lastModifiedTimeTxt = "2016-05-02T14:14:14Z";
        FileTime lastModifiedTime = FileTime.from(Instant.parse(lastModifiedTimeTxt));
        Files.setLastModifiedTime(file,lastModifiedTime);

        // AccessTime
        // Same problem than with modified time
        // We then set it first
        String lastAccessTimeTxt = "2016-05-02T13:13:13Z";
        FileTime lastAccessTime = FileTime.from(Instant.parse(lastAccessTimeTxt));
        Files.setAttribute(file, "basic:lastAccessTime", lastAccessTime, java.nio.file.LinkOption.NOFOLLOW_LINKS);

        // The default permission may be not the same
        // As they depends of the process permission when the file is created
        // We set them first
        Set<PosixFilePermission> expectedPermission = new HashSet<PosixFilePermission>();

        expectedPermission.add(PosixFilePermission.GROUP_READ);
        expectedPermission.add(PosixFilePermission.GROUP_WRITE);
        expectedPermission.add(PosixFilePermission.GROUP_EXECUTE);
        //expectedPermission.add(PosixFilePermission.OTHERS_READ);
        //expectedPermission.add(PosixFilePermission.OTHERS_WRITE);
        //expectedPermission.add(PosixFilePermission.OTHERS_EXECUTE);
        expectedPermission.add(PosixFilePermission.OWNER_READ);
        expectedPermission.add(PosixFilePermission.OWNER_WRITE);
        expectedPermission.add(PosixFilePermission.OWNER_EXECUTE);

        if (!testFileSystem.isWindows()) {
            Files.setPosixFilePermissions(file, expectedPermission);
        }

        // The test can start
        PosixFileAttributes attrs = Files.readAttributes(file, PosixFileAttributes.class);
        assertNotNull("The file exist, we must get attributes", attrs);
        assertFalse("This is not a directory", attrs.isDirectory());
        assertTrue("This is a regular file", attrs.isRegularFile());
        assertFalse("This is not an symbolic link", attrs.isSymbolicLink());
        assertFalse("This is not an other file", attrs.isOther());

        // The file size is OS file system dependent.
        //  80 on Windows
        //  78 on Linux
        assertTrue("The file size is below or equal to 80", 80 >= attrs.size());
        assertTrue("The file size is bigger or equal to 78", attrs.size() >= 78 );

        assertEquals("The last modified time is: ", lastModifiedTimeTxt, attrs.lastModifiedTime().toString());
        assertEquals("The last modified time is the creation time (Creation time doesn't exist in SFTP", attrs.creationTime(), attrs.lastModifiedTime());
        assertEquals("The last access time is ", lastAccessTimeTxt, attrs.lastAccessTime().toString());

        if (!testFileSystem.isWindows()) {
            // Let op on Windows with the MockSsh Sftp Server, this will fail but not on SSH on Linux
            // Windows unfortunately doesn't support POSIX file systems
            assertEquals("The permissions are equal", expectedPermission, attrs.permissions());
        }

    }

}
