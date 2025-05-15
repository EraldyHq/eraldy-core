package net.bytle.niofs.sftp;

import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gerard on 18-05-2016.
 * <p/>
 * The externalResource Junit Rule that gives the Sftp File System external resources for the test class that need them
 */
public class ExternalResourceFileSystemsRule extends ExternalResource {


    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());


    /**
     * The external SFTP file system
     */
    private static FileSystem externalSftpFileSystem = null;
    private static String externalSftpFileSystemURLWithoutPath;
    private static String externalSftpFileSystemHomeDir;

    /**
     * Counter to know if we are in a test series to not close the ressources
     * in the first call of the after method
     */
    private static Integer counterConstructorCall = 0;
    /**
     * Counter that is used in the get method ({@link #getFileSystemURL()} and {@link #getIterable()}) for the parametrized test to instantiate the array
     * with the good number of file system
     */
    private static Integer counterSftpServerInstantiation = 0;


    /**
     * The initialization of the external docker sshd sftp server
     */
    public ExternalResourceFileSystemsRule() {

        // The constructor must not return an exception
        // As it's used as static member in the test classes
        try {

            counterConstructorCall++;

            if (counterConstructorCall == 1) {


                LOGGER.info("Init of the server properties and file system");

                // Environment
                Map<String, String> environments = System.getenv();

                // External File System
                // The default docker sshd url
                externalSftpFileSystemURLWithoutPath = "sftp://root:welcome@localhost";
                // The home user dir can be found dynamically but to test the Paths operations, we need to set an absolute path
                // and therefore we need to known the home user directory before making a connection.
                externalSftpFileSystemHomeDir = "/root";


                URI uri = URI.create(externalSftpFileSystemURLWithoutPath + externalSftpFileSystemHomeDir);
                externalSftpFileSystem = FileSystems.newFileSystem(uri, null);

                // Test if we can make a connection
                // To avoid that Junit will catch an error on every test
                // and retry on every test to make a connection
                // Which has as effect to ban the connection
                try {
                    Files.exists(externalSftpFileSystem.getPath("Unknown"));
                } catch (Exception e) {
                    String msg = "Error on the connection (" + e.getLocalizedMessage() + ") to " + SftpURIUtil.toStringWithoutPassword(uri);
                    LOGGER.severe(msg);
                    System.out.println(msg);
                    System.exit(1);
                }

                counterSftpServerInstantiation++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * @return if the Mock Server is on windows
     */
    public static boolean isWindows() {
        if (System.getProperty("os.name").contains("Windows")) {
            return true;
        } else {
            return false;
        }
    }

    public void close() {

        // Stop the external File System
        closeFileSystem(externalSftpFileSystem);

    }

    private void closeFileSystem(FileSystem fileSystem) {
        try {
            if (fileSystem != null) {
                fileSystem.close();
                fileSystem = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Nothing to do here, the connection opens with IO operations.
     *
     * @throws Throwable
     */
    @Override
    protected void before() throws Throwable {


    }

    @Override
    protected void after() {

        if (counterConstructorCall > 1) {
            counterConstructorCall--;
        } else {
            close();
            LOGGER.info("Close of the file systems and the mock server");
        }


    }

    /**
     * Most of the parameterized test use this iterable
     *
     * @return the file system to test
     * TODO: ? (only one) go back to a non-parametrized test ?
     */
    public List<FileSystem> getIterable() {

        List<FileSystem> fileSystemList = new ArrayList<>();
        if (externalSftpFileSystem != null) {
            fileSystemList.add(externalSftpFileSystem);
        }
        return fileSystemList;

    }


    /**
     * For the test {@link PathsTest}
     *
     * @return the URL and the expected home directory
     */
    public Iterable<String[]> getFileSystemURL() {

        String[][] arrayObject = new String[counterSftpServerInstantiation][2];
        int indexInArray = 0;
        if (externalSftpFileSystem != null) {
            arrayObject[indexInArray][0] = externalSftpFileSystemURLWithoutPath;
            arrayObject[indexInArray][1] = externalSftpFileSystemHomeDir;
            indexInArray++;
        }
        return Arrays.asList(arrayObject);
    }
}
