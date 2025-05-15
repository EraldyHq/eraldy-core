package net.bytle.niofs.sftp;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.assertEquals;

/**
 * Created by gerard on 23-05-2016.
 * Test of the {@link Files} functions excepted for the function that modifies the attributes. See {@link FilesAttributesTest}
 */
@RunWith(value = Parameterized.class)
public class FilesTest {

    // The set of test (and/of parameters) is declared below
    // The name argument below will be present in the Junit output
    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<FileSystem> data() {

        return testFileSystem.getIterable();

    }

    @ClassRule
    public static ExternalResourceFileSystemsRule testFileSystem = new ExternalResourceFileSystemsRule();



    private FileSystem fileSystem;


    public FilesTest(FileSystem fileSystem) throws IOException {
        this.fileSystem = fileSystem;
        Path dst = fileSystem.getPath(FilesUtil.TARGET_BASE_DIR_RUN, FilesUtil.TARGET_BASE_DIR_TEST);
        if (Files.exists(dst)) {
            deleteDirectory(dst);
        }
    }

    /**
     * If the file does not exist in the target system
     * if must returns a {@link NoSuchFileException}
     * @throws IOException
     */
    @Test(expected = NoSuchFileException.class)
    public void copyFromLocalToNonExistentFile() throws IOException {
        Path readMeFile = Paths.get("README.md");
        Path targetFile = fileSystem.getPath(FilesUtil.TARGET_BASE_DIR_RUN, FilesUtil.TARGET_BASE_DIR_TEST,"doesntexist","doesntexist.md");
        Files.copy(readMeFile,targetFile);
    }

    @Test
    public void CopyFromLocalToSftpTest() throws IOException {


        Path src = Paths.get("README.md");
        Path dst = fileSystem.getPath(FilesUtil.TARGET_BASE_DIR_RUN, FilesUtil.TARGET_BASE_DIR_TEST,"whatever","CopyFromLocalToSftp.md");
        FilesUtil.copyAlways(src,dst);
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);

    }

    @Test
    public void notExistsTest() throws IOException {
        Path dst = fileSystem.getPath(FilesUtil.TARGET_BASE_DIR_RUN, FilesUtil.TARGET_BASE_DIR_TEST,"doesntexist","doesntexist.md");
        boolean actual = Files.notExists(dst);
        assertEquals("The file must not exist",true, actual);
    }

    @Test
    public void ExistsTest() throws IOException {
        Path dst = fileSystem.getPath(FilesUtil.TARGET_BASE_DIR_RUN, FilesUtil.TARGET_BASE_DIR_TEST,"doesntexist","doesntexist.md");
        assertEquals("The file must not exist",false,Files.exists(dst));
    }

    @Test
    public void CreateFileTest() throws IOException {

        Path dst = fileSystem.getPath(FilesUtil.TARGET_BASE_DIR_RUN, FilesUtil.TARGET_BASE_DIR_TEST, "CreateFileTest.md");
        Path parent = dst.getParent();
        if (Files.notExists(parent)){
            Files.createDirectories(parent);
        }
        Files.createFile(dst);

    }

    /*
     * A blank relative path must be a directory.
     */
    @Test
    public void isDirectoryTest() throws IOException {

        Path dst = fileSystem.getPath("");
        boolean isDirectory = Files.isDirectory(dst);
        assertEquals("This is a directory",true, isDirectory);

    }

    /**
     * Will delete always a directory even if it's not empty
     * @param path
     * @throws IOException
     */
    static void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }



}
