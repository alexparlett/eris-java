package org.homonoia.sw;

import org.homonoia.sw.application.Game;
import org.homonoia.sw.core.CommandLineArgs;
import org.homonoia.sw.core.CommandLineArgsParser;
import org.homonoia.sw.core.FileSystem;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.prefs.BackingStoreException;

/**
 * Copyright (c) 20152017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 12/04/2017
 */
public class Main {
    public static void main(String[] args) throws BackingStoreException, ClassNotFoundException {
        CommandLineArgs commandLineArgs = CommandLineArgsParser.parse(args);
        SLF4JBridgeHandler.install();
        System.setProperty("log.APP_DIR", FileSystem.getApplicationDataDirectoryString());
        System.setProperty("log.LEVEL", commandLineArgs.containsOption("debug") ? "TRACE" : "INFO");

        Game game = new Game(commandLineArgs);
        game.start();
    }
}
