package net.bytle.docExec;

/**
 * An example of a {@link DocExecutorUnit#addMainClass(String, Class)} MainClass}
 * implementing a basic echo appHome
 * <p>
 * This class is used for testing purpose
 * <p>
 * In the documentation, you would see something like that
 * <p>
 * echo Hello Nico
 */
public class DocCommandEcho {

    public static void main(String[] args) {
        System.out.println(String.join("", args));
    }

}
