package org.homonoia.eris.engine;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.ExitCode;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.core.components.Clock;
import org.homonoia.eris.io.FileSystem;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.core.ExitRequested;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.io.Input;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.ini.IniException;
import org.homonoia.eris.resources.types.json.JsonException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created by alexp on 25/02/2016.
 */
@ContextualComponent
public class Engine extends Contextual {

    @Autowired
    private ResourceCache resourceCache;

    @Autowired
    private Graphics graphics;

    @Autowired
    private Clock clock;

    @Autowired
    private Renderer renderer;

    @Autowired
    private Settings settings;

    @Autowired
    private Locale locale;

    @Autowired
    private FileSystem fileSystem;

    @Autowired
    private Log log;

    @Autowired
    private Input input;

    /**
     * Instantiates a new Engine.
     *
     * @param context the context
     */
    @Autowired
    public Engine(final Context context) {
        super(context);

        subscribe(this::handleExitRequest, ExitRequested.class);
    }

    public void initialize() throws InitializationException {
        // Initialize Base Components
        initializationLog();

        fileSystem.addPath(fileSystem.getApplicationDataDirectory());
        fileSystem.addPath(fileSystem.getApplicationDirectory());
        fileSystem.addPath(fileSystem.getTempDirectory());

        resourceCache.addDirectory(fileSystem.getApplicationDataDirectory());
        resourceCache.addDirectory(fileSystem.getApplicationDirectory().resolve("Data"));

        // Load Settings and Initialize Components
        try {
            settings.load();
        } catch (IOException | IniException e) {
            e.printStackTrace();
        }

        try {
            locale.load(settings.getString("Game", "Language").orElse("en_GB"));
        } catch (IOException | JsonException e) {
            e.printStackTrace();
        }

        // Initialize Window
        GLFW.glfwSetErrorCallback(GLFWErrorCallback.create(this::handleGLFWError));
        if (GLFW.glfwInit() != GLFW.GLFW_TRUE) {
            throw new InitializationException("Failed to initialize GLFW.", ExitCode.GLFW_CREATE_ERROR);
        }

        graphics.setTitle("Eris");
        graphics.setIcon("icon.ico");
        graphics.setResizable(settings.getBoolean("Graphics", "Resizable").orElse(false));
        graphics.setSize(settings.getInteger("Graphics", "Width").orElse(1024), settings.getInteger("Graphics", "Height").orElse(768));
        graphics.setBorderless(settings.getBoolean("Graphics", "Borderless").orElse(false));
        graphics.setFullscreen(settings.getBoolean("Graphics", "Fullscreen").orElse(true));
        graphics.setVSync(settings.getBoolean("Graphics", "VSync").orElse(true));
        graphics.setSamples(settings.getInteger("Graphics", "Multisamples").orElse(4));
        graphics.setGamma(settings.getFloat("Graphics", "Gamma").orElse(1.f));

        graphics.initialize();
        renderer.initialize();
        input.initialize();

        graphics.show();
    }

    public void run() {
    }

    public void shutdown() {
        graphics.hide();

        renderer.shutdown();
        resourceCache.shutdown();
        graphics.shutdown();

        double elapsedTime = clock.getElapsedTime();
        int frameNumber = clock.getFrameNumber();

        GLFW.glfwTerminate();

        shutdownLog(elapsedTime, frameNumber);
    }

    private void initializationLog() {
        log.initialize();
    }

    private void shutdownLog(final double elapsedTime, final int frameNumber) {
        log.shutdown();
    }

    private void handleExitRequest(final ExitRequested exitRequest) {
    }

    private void handleGLFWError(int error, long description) {

    }
}
