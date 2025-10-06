package net.bytle.niofs.sftp;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;

import static org.junit.Assert.assertEquals;


/**
 * Created by gerard on 23-05-2016.
 * The path operations of the class {@link Paths}
 */
@RunWith(value = Parameterized.class)
public class PathsTest {




    // The set of test (and/of parameters) is declared below
    // The name argument below will be present in the Junit output
    @Parameterized.Parameters(name = "{index}: {0} {1}")
    public static Iterable<String[]> data() {

        return externalResourceFileSystems.getFileSystemURL();

    }

    @ClassRule
    public static ExternalResourceFileSystemsRule externalResourceFileSystems = new ExternalResourceFileSystemsRule();

    private final String fileSystemUrl;
    private final String fileSystemUserDirectory;

    public PathsTest(String url, String userDirectory) throws IOException {
        this.fileSystemUrl = url;
        this.fileSystemUserDirectory = userDirectory;

        FileSystem fileSystem = FileSystems.getFileSystem(URI.create(fileSystemUrl + fileSystemUserDirectory));
        Path src = Paths.get("src","test","resources","sftp", this.getClass().getSimpleName()+".md");
        Path dest = fileSystem.getPath(FilesUtil.TARGET_BASE_DIR_RUN, FilesUtil.TARGET_BASE_DIR_TEST, this.getClass().getSimpleName(), this.getClass().getSimpleName()+ ".md");
        FilesUtil.copyAlways(src,dest);
    }

    @Test
    public void getWithPath() throws IOException {


        // The url must be absolute
        String url = fileSystemUrl + fileSystemUserDirectory + "/"+ FilesUtil.TARGET_BASE_DIR_RUN + "/"+ FilesUtil.TARGET_BASE_DIR_TEST + "/" + this.getClass().getSimpleName() + "/" + this.getClass().getSimpleName()+ ".md";
        Path path = Paths.get(URI.create(url));
        assertEquals("The file ("+url+") must exist",true, Files.exists(path));
        path.getFileSystem().close();

    }

    /**
     *
     *
     * @throws IOException
     */
    @Test
    public void getWithoutPath() throws IOException {

        // The url must be absolute
        String url = fileSystemUrl;
        Path path = Paths.get(URI.create(url));
        assertEquals("Without path, it must take the user home directory", fileSystemUserDirectory, path.toAbsolutePath().toString());
        path.getFileSystem().close();


    }


}
