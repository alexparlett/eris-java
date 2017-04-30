package org.homonoia.sw;

import org.homonoia.sw.core.CommandLineArgs;
import org.homonoia.sw.core.CommandLineArgsParser;
import org.homonoia.sw.application.Application;

/**
 * Copyright (c) 20152017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 12/04/2017
 */
public class Main {
    public static void main(String[] args) {
        CommandLineArgs commandLineArgs = CommandLineArgsParser.parse(args);

        Application application = new Application(commandLineArgs);
        application.start();
    }
}
