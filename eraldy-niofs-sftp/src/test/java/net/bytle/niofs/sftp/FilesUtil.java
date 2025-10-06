package net.bytle.niofs.sftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by gerard on 18-06-2016.
 * A class that hold all file test:
 *
 *   * variable, constant
 *   * and utilities functions
 *
 */
public class FilesUtil {

    /**
     * The base directory for all test manipulations in this class
     */
    protected static final String TARGET_BASE_DIR_RUN = "target";
    protected static final String TARGET_BASE_DIR_TEST = "FilesTest.dir";


    public static void copyAlways(Path source, Path target) throws IOException {
        if (!Files.exists(target)) {
            Path parent = target.getParent();
            if (Files.notExists(parent)){
                Files.createDirectories(parent);
            }
            Files.createFile(target);
        }
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }


}
