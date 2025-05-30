package net.bytle.docExec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocShell {

    public static final String DOS_LANG = "dos";
    public static final String BASH_LANG = "bash";

    /**
     * @param docUnit   - the docTestUnit
     * @param shellLang - the lang (dos or bash)
     * @return Building the commands (each command is represented as an array of args)
     */
    protected static List<String[]> parseShellCommand(DocUnit docUnit, String shellLang) {

        final int defaultState = 1;
        final int spaceCapture = 2;
        final int quoteCapture = 3;
        final char spaceChar = ' ';
        final char quoteChar = '"';
        char[] comments;
        switch (shellLang) {
            case DOS_LANG:
                comments = new char[]{':', ':'};
                break;
            case BASH_LANG:
                comments = new char[]{'#'};
                break;
            default:
                throw new RuntimeException("Unsupported lang " + shellLang);

        }


        List<String[]> commands = new ArrayList<>();
        String[] lines = docUnit.getCode().trim().split("\n|\r\n");
        for (String line : lines) {

            line = line.trim();

            // Comments skipping
            if (line.length() >= comments.length) {
                if (line.trim().substring(0, comments.length).equals(new String(comments))) {
                    continue;
                }
            }

            int state = defaultState;
            char[] dst = new char[line.length()];
            line.getChars(0, line.length(), dst, 0);
            StringBuilder arg = new StringBuilder();
            List<String> args = new ArrayList<>();
            for (char c : dst) {
                switch (state) {
                    case defaultState:
                        switch (c) {
                            case spaceChar:
                                state = spaceCapture;
                                continue;
                            case quoteChar:
                                state = quoteCapture;
                                break;
                            default:
                                arg.append(c);
                                state = spaceCapture;
                                break;
                        }
                        break;
                    case spaceCapture:
                        switch (c) {
                            case spaceChar:
                                if (!arg.toString().isEmpty()) {
                                    args.add(arg.toString());
                                    arg = new StringBuilder();
                                }
                                state = defaultState;
                                break;
                            case quoteChar:
                                if (!arg.toString().isEmpty()) {
                                    arg.append(c);
                                } else {
                                    state = quoteCapture;
                                }
                                break;
                            default:
                                arg.append(c);
                                break;
                        }
                        break;
                    case quoteCapture:
                        switch (c) {
                            case quoteChar:
                                if (!arg.toString().isEmpty()) {
                                    args.add(arg.toString());
                                    arg = new StringBuilder();
                                }
                                state = defaultState;
                                break;
                            default:
                                arg.append(c);
                                break;
                        }
                        break;
                }
            }

            if (!arg.toString().trim().isEmpty()) {
                args.add(arg.toString());
            }
            commands.add(args.toArray(new String[0]));
        }

        for (String[] args : commands) {

            // Env variable expansion
            // Declared
            for (Map.Entry<String, String> entry : docUnit.getEnv().entrySet()) {
                for (int i = 0; i < args.length; i++) {
                    if (shellLang.equals(DOS_LANG)) {
                        args[i] = args[i].replace("%" + entry.getKey() + "%", entry.getValue());
                    }
                    if (shellLang.equals(BASH_LANG)) {
                        args[i] = args[i].replace("$" + entry.getKey(), entry.getValue());
                        args[i] = args[i].replace("${" + entry.getKey() + "}", entry.getValue());
                    }
                }
            }
            // Env
            for (String envName : System.getenv().keySet()) {
                for (int i = 0; i < args.length; i++) {
                    if (shellLang.equals(DOS_LANG)) {
                        args[i] = args[i].replace("%" + envName + "%", System.getenv().get(envName));
                    }
                    if (shellLang.equals(BASH_LANG)) {
                        args[i] = args[i].replace("$" + envName, System.getenv().get(envName));
                        args[i] = args[i].replace("${" + envName + "}", System.getenv().get(envName));
                    }
                }
            }

            // Escaping (after env expansion)
            for (int i = 0; i < args.length; i++) {

                // Path in DOS must have two slash in the code to escape it
                args[i] = args[i].replace("\\", "\\\\");

            }

        }
        return commands;
    }


}
