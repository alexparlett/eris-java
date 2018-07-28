package org.homonoia.sw.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner;
import org.apache.commons.cli.ParseException;
import org.homonoia.sw.SolarianWarsGame;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher {
    public static void main(String[] args) throws ParseException {
        createApplication(args);
    }

    private static Lwjgl3Application createApplication(String[] args) throws ParseException {
        return new Lwjgl3Application(new SolarianWarsGame(args, new DesktopClassScanner()),
                getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("SolarianWars");
        configuration.setMaximized(true);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}