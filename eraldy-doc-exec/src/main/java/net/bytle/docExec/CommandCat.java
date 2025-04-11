package net.bytle.docExec;

import net.bytle.fs.Fs;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An example of a {@link DocExecutorUnit#addMainClass(String, Class)} MainClass}
 * implementing a basic cat command
 * <p>
 * This class is used for testing purpose
 * <p>
 * In the documentation, you would see something like that
 * <p>
 * cat file.txt
 */
public class CommandCat {

    public static void main(String[] args) {

        Path path = Paths.get(args[0]);
        try {
            System.out.println(Fs.getFileContent(path));
        } catch (NoSuchFileException e) {
            System.err.println("Error: The File (" + path + ") was not found");
            System.exit(1);
        }
    }

}
