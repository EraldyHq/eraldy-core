package net.bytle.java;

import net.bytle.type.env.OsEnvs;

import java.lang.management.ManagementFactory;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;

public class JavaEnvs {


    static String SYSTEM_PROPERTY_NAME = "web.environment";
    static String ENV_VARIABLE_NAME = "WEB_ENVIRONMENT";

    public static final Path HOME_PATH;
    public static final boolean IS_IDE_DEBUGGING;
    private static Boolean IS_DEV;
    private static Boolean IS_TEST;

    static {

        /*
         * Home path and is Dev mode
         */
        Path sourceCodePath = Javas.getSourceCodePath(JavaEnvs.class);
        Path homePath = sourceCodePath.getParent().getParent();


        /*
         * If the mode is debugging, this value is true
         * (Works in eclipse, idea)
         * It's used mostly to disable the timeout that will otherwise
         * kick you out of a debug session.
         * https://stackoverflow.com/questions/1109019/determine-if-a-java-application-is-in-debug-mode-in-eclipse
         */
        String inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
        IS_IDE_DEBUGGING = inputArguments.contains("-agentlib:jdwp");


        HOME_PATH = homePath;


    }

    /**
     * @param clazz - the class to get the location (ie to see if the class is in a build directory)
     * @return true
     */
    public static boolean isDev(Class<?> clazz) {

        if (IS_DEV != null) {
            return IS_DEV;
        }

        /*
         *  On an idea of
         *  https://github.com/vert-x3/vertx-web/blob/master/vertx-web-common/src/main/java/io/vertx/ext/web/common/WebEnvironment.java
         */
        String env = System.getProperty(SYSTEM_PROPERTY_NAME, System.getenv(ENV_VARIABLE_NAME));
        if ("dev".equalsIgnoreCase(env) || "Development".equalsIgnoreCase(env)) {
            IS_DEV = true;
            return true;
        }
        /*
         * For vertx, the Dev mode is :
         * * with the VERTXWEB_ENVIRONMENT environment variable
         * * or `vertxweb.environment` system property
         * set to dev.
         *
         */
        env = OsEnvs.getEnvOrDefault("VERTXWEB_ENVIRONMENT", "prod");
        if ("dev".equalsIgnoreCase(env) || "Development".equalsIgnoreCase(env)) {
            IS_DEV = true;
            return true;
        }

        try {
            Javas.getBuildDirectory(clazz);
            IS_DEV = true;
            return true;
        } catch (NotDirectoryException e) {
            // ok
        }

        IS_DEV = false;
        return false;

    }

    @SuppressWarnings("ConstantValue")
    public static boolean isJUnitTest() {
        if (IS_TEST != null) {
            return IS_TEST;
        }
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                IS_TEST = true;
                return IS_TEST;
            }
        }
        IS_TEST = false;
        return IS_TEST;
    }


}
