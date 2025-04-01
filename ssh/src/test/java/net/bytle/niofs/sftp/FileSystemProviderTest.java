package net.bytle.niofs.sftp;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;

import static org.junit.Assert.assertEquals;

/**
 * Created by gerard on 16-06-2016.
 */
public class FileSystemProviderTest {

    @Test
    public void sftpFileSystemIsInstalled() {

        // Get the SFTP File System Provider
        // See http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
        // To know if the file system is loaded through the class loader
        FileSystemProvider sftpFileSystemProvider = null;
        for (FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
            if (SftpFileSystemProvider.SFTP_SCHEME.equals(fileSystemProvider.getScheme())) {
                sftpFileSystemProvider = fileSystemProvider;
            }
        }
        org.junit.Assert.assertNotNull("Unable to get a " + SftpFileSystemProvider.SFTP_SCHEME + " file system provider", sftpFileSystemProvider);

    }

    /**
     * To show how to get a file system with {@link FileSystems}
     */
    @Test
    public void sftpFileSystemIsInstalledVersion2WithMinimalURL() throws IOException {

        // Get the SFTP File System Provider
        // See http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
        // To know if the file system is loaded through the class loader

        String minimalURI = SftpFileSystemProvider.SFTP_SCHEME + ":/";
        FileSystem sftpFileSystemProvider = FileSystems.newFileSystem(URI.create(minimalURI), null);

        org.junit.Assert.assertNotNull("Unable to get a " + SftpFileSystemProvider.SFTP_SCHEME + " file system provider with the minimal URI ("+minimalURI+")", sftpFileSystemProvider);


    }



}
