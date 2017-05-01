package org.homonoia.sw.application;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioListenerState;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.system.AppSettings;
import com.jme3.texture.plugins.AWTLoader;
import lombok.Getter;
import org.homonoia.sw.assets.loaders.LocaleLoader;
import org.homonoia.sw.core.CommandLineArgs;
import org.homonoia.sw.core.FileSystem;
import org.homonoia.sw.scripting.ScriptClassLoader;
import org.homonoia.sw.states.LoadingAppState;

import java.util.prefs.BackingStoreException;

import static com.jme3.system.AppSettings.LWJGL_OPENAL;
import static com.jme3.system.AppSettings.LWJGL_OPENGL3;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/04/2017
 */
@Getter
public class Game extends SimpleApplication {

    private final CommandLineArgs commandLineArgs;
    private final ScriptClassLoader scriptClassLoader;
    private NiftyJmeDisplay niftyJmeDisplay;
    private FilterPostProcessor filterPostProcessor;

    public Game(CommandLineArgs commandLineArgs) throws BackingStoreException, ClassNotFoundException {
        super(new AudioListenerState(), new FlyCamAppState());

        this.commandLineArgs = commandLineArgs;
        this.scriptClassLoader = new ScriptClassLoader();

        if (commandLineArgs.containsOption("debug")) {
            stateManager.attach(new StatsAppState());
            stateManager.attach(new DebugKeysAppState());
        }

        settings = new AppSettings(true);
        settings.setRenderer(LWJGL_OPENGL3);
        settings.setAudioRenderer(LWJGL_OPENAL);
        settings.setTitle("Solarian Wars");
        settings.putString("Language", "enGB");

        timer = new NiftyNanoTimer();

        //Fixes bug on Mac if done later
        Class.forName(AWTLoader.class.getName());
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator(FileSystem.getApplicationDirectoryString(), FileLocator.class);
        assetManager.registerLocator(FileSystem.getApplicationDataDirectoryString(), FileLocator.class);
        assetManager.registerLoader(LocaleLoader.class, "lang");

        scriptClassLoader.registerDirectory(FileSystem.getApplicationDirectory());
        scriptClassLoader.registerDirectory(FileSystem.getApplicationDataDirectory());

        niftyJmeDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        guiViewPort.addProcessor(niftyJmeDisplay);

        stateManager.attach(new LoadingAppState());
    }


}
