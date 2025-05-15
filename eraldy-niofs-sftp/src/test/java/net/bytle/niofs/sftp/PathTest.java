package net.bytle.niofs.sftp;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by gerard on 23-05-2016.
 * The path operations of the class {@link Path}
 * <p/>
 * Most of the below test can be found in the tutorial with the Solaris Syntax
 *
 * @see <a href="http://docs.oracle.com/javase/tutorial/essential/io/pathOps.html">Path Ops Tutorial</a>
 */
public class PathTest {


    /**
     * The path operation for an absolute path.
     * see <a href="http://docs.oracle.com/javase/tutorial/essential/io/pathOps.html">Path Ops Tutorial</a>
     *
     * @throws IOException
     */
    @Test
    public void getOpsForAbsolutePath() throws IOException {

        //
        // Solaris syntax

        URI uri = URI.create("sftp:/home/joe/foo");
        Path path = Paths.get(uri);


        //        System.out.format("toString: %s%n", path.toString());
        //        System.out.format("getFileName: %s%n", path.getFileName());
        //        System.out.format("getName(0): %s%n", path.getName(0));
        //        System.out.format("getNameCount: %d%n", path.getNameCount());
        //        System.out.format("subpath(0,2): %s%n", path.subpath(0,2));
        //        System.out.format("getParent: %s%n", path.getParent());
        //        System.out.format("getRoot: %s%n", path.getRoot());

        assertEquals("path.toString", "/home/joe/foo", path.toString());
        assertEquals("path.getFileName", "foo", path.getFileName().toString());
        assertEquals("path.getName(0)", "home", path.getName(0).toString());
        assertEquals("path.getNameCount", 3, path.getNameCount());
        assertEquals("path.subpath", "home/joe", path.subpath(0, 2).toString());
        assertEquals("path.getParent()", "/home/joe", path.getParent().toString());
        assertEquals("path.getRoot", "/", path.getRoot().toString());

    }

    /**
     * The path operation for an absolute path.
     * see <a href="http://docs.oracle.com/javase/tutorial/essential/io/pathOps.html">Path Ops Tutorial</a>
     * TODO: Possibility to set the working directory through a query parameters. ?working.directory=./..
     *
     * @throws IOException
     */
    @Test
    public void getOpsForRelativePath() throws IOException, URISyntaxException {


        FileSystem sftpFileSystem = FileSystems.newFileSystem(URI.create("sftp:/a/fake/working"), null);
        Path path = sftpFileSystem.getPath("sally/bar");


        //        System.out.format("toString: %s%n", path.toString());
        //        System.out.format("getFileName: %s%n", path.getFileName());
        //        System.out.format("getName(0): %s%n", path.getName(0));
        //        System.out.format("getNameCount: %d%n", path.getNameCount());
        //        System.out.format("subpath(0,2): %s%n", path.subpath(0,2));
        //        System.out.format("getParent: %s%n", path.getParent());
        //        System.out.format("getRoot: %s%n", path.getRoot());

        assertEquals("path.toString", "sally/bar", path.toString());
        assertEquals("path.getFileName", "bar", path.getFileName().toString());
        assertEquals("path.getName(0)", "sally", path.getName(0).toString());
        assertEquals("path.getNameCount", 2, path.getNameCount());
        assertEquals("path.subpath", "sally", path.subpath(0, 1).toString());
        assertEquals("path.getParent()", "sally", path.getParent().toString());
        assertEquals("path.getRoot", null, path.getRoot());

    }

}
