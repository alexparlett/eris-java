package org.homonoia.sw;

import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.homonoia.eris.engine.Log;
import org.homonoia.sw.application.Application;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.CommandLineArgsParser;

/**
 * Copyright (c) 20152017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 12/04/2017
 */
public class Main {
    public static void main(String[] args) throws InitializationException, MissingRequiredComponentException {
        CommandLineArgs commandLineArgs = CommandLineArgsParser.parse(args);
        Log.initialize(commandLineArgs.containsOption("debug"));

        Application application = new Application(commandLineArgs);
        application.startup();
        application.run();
        application.shutdown();
    }
}
