package org.homonoia.sw.application;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioListenerState;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.FileSystem;

import static com.jme3.system.AppSettings.LWJGL_OPENGL3;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/04/2017
 */
public class Application extends SimpleApplication {

    private final CommandLineArgs commandLineArgs;

    public Application(CommandLineArgs commandLineArgs) {
        super(new AudioListenerState());
        this.commandLineArgs = commandLineArgs;

        if (commandLineArgs.containsOption("debug")) {
            stateManager.attach(new StatsAppState());
            stateManager.attach(new DebugKeysAppState());
        }

        settings.setRenderer(LWJGL_OPENGL3);
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator(FileSystem.getApplicationDirectory(), FileLocator.class);
        assetManager.registerLocator(FileSystem.getApplicationDataDirectory(), FileLocator.class);
    }
}
