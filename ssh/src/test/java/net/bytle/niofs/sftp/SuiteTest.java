package net.bytle.niofs.sftp;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by gerard on 17-06-2016.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FilesAttributesTest.class,
        FilesTest.class,
        FileSystemProviderTest.class,
        FileSystemTest.class,
        FileVisitorTest.class,
        PathsTest.class
        , PathTest.class //does need a connection
})
public class SuiteTest {

    @ClassRule
    public static ExternalResourceFileSystemsRule testFileSystem = new ExternalResourceFileSystemsRule();

}
