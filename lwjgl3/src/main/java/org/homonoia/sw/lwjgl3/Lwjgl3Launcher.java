package org.homonoia.sw.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import org.homonoia.sw.SolarianWarsGame;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication(args);
    }

    private static Lwjgl3Application createApplication(String[] args) {
        SolarianWarsGame.args = args;

        return new Lwjgl3Application(new AutumnApplication(new DesktopClassScanner(), SolarianWarsGame.class),
                getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("SolarianWars");
        configuration.setWindowedMode(SolarianWarsGame.WIDTH, SolarianWarsGame.HEIGHT);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}