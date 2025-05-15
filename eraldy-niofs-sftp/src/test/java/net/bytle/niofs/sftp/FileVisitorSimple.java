package net.bytle.niofs.sftp;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by gerard on 18-05-2016.
 * A visitor for the test {@link FileVisitorTest}
 */
public class FileVisitorSimple implements FileVisitor<Path> {


    private Boolean printDetails = false;
    private int counterFile =0;

    public FileVisitorSimple(Boolean printFiles) {
        this.printDetails = printFiles;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        if (printDetails) {
            System.out.println("preVisitDirectory: " + dir.getFileName());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        counterFile++;
        if (counterFile == 1) {
            System.out.println("visitFile: First");
        }
        if (counterFile % 100 == 0) {
            System.out.println("visitFile: "+counterFile+" files where visited");
        }
        if (printDetails) {
            System.out.println("visitFile: " + file.getFileName());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (printDetails) {
            System.out.println("visitFileFailed: " + file.getFileName());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (printDetails) {
            System.out.println("postVisitDirectory: " + dir.getFileName());
        }
        return FileVisitResult.CONTINUE;
    }

}
