package org.homonoia.sw;

import com.badlogic.gdx.physics.bullet.Bullet;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.scanner.ClassScanner;
import org.apache.commons.cli.CommandLine;
import org.homonoia.sw.configuration.ProgramArguments;
import org.homonoia.sw.mvc.application.AutumnApplication;

/**
 * This class serves only as the application scanning root. Any classes in its package (or any of the sub-packages)
 * with proper Autumn MVC annotations will be found, scanned and initiated.
 */
public class SolarianWarsGame extends AutumnApplication {
    /**
     * Default application size.
     */
    public static final int WIDTH = 1600, HEIGHT = 1024;

    public final CommandLine args;

    public SolarianWarsGame(ClassScanner componentScanner, final CommandLine args) {
        super(componentScanner, SolarianWarsGame.class);
        this.args = args;
        Bullet.init(true, true);
    }

    @Override
    protected void addDefaultComponents(ContextInitializer initializer) {
        super.addDefaultComponents(initializer);
        initializer.addComponent(new ProgramArguments(args));
    }
}