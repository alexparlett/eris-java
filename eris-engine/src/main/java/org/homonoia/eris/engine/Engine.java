package org.homonoia.eris.engine;


import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.ExitCode;
import org.homonoia.eris.core.components.Clock;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.core.ExitRequested;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.input.Input;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.json.JsonException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
public class Engine extends Contextual {

    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private AtomicBoolean shouldExit = new AtomicBoolean(false);

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

        fileSystem.addPath(FileSystem.getApplicationDataDirectory());
        fileSystem.addPath(FileSystem.getApplicationDirectory());
        fileSystem.addPath(FileSystem.getTempDirectory());

        resourceCache.addDirectory(FileSystem.getApplicationDataDirectory());
        resourceCache.addDirectory(FileSystem.getApplicationDirectory());
        resourceCache.addDirectory(FileSystem.getApplicationDirectory().resolve("Data"));

        // Load Settings and Initialize Components
        try {
            settings.load();
        } catch (IOException e) {
            throw new InitializationException("Failed to load Settings.", ExitCode.FATAL_ERROR, e);
        }

        try {
            locale.load(settings.getString("Game", "Language").orElse("en_GB"));
        } catch (IOException | JsonException e) {
            throw new InitializationException("Failed to load Locale.", ExitCode.FATAL_ERROR, e);
        }

        // Initialize Window
        GLFW.glfwSetErrorCallback(GLFWErrorCallback.create(this::handleGLFWError));
        if (GLFW.glfwInit() != GLFW.GLFW_TRUE || !getContext().getExitCode().equals(ExitCode.SUCCESS)) {
            throw new InitializationException("Failed to initialize GLFW.", ExitCode.GLFW_CREATE_ERROR);
        }

        graphics.setTitle(settings.getString("Game", "Title").orElse("Eris"));
        graphics.setIcon(settings.getString("Game", "Icon").orElse("icon.ico"));
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

        while (!shouldExit.get()) {

        }

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

        LOG.info("Initializing...");
        LOG.info("\tOS: {}", System.getProperty("os.name"));
        LOG.info("\tArch: {}", System.getProperty("os.arch"));
        LOG.info("\tCores: {}", Runtime.getRuntime().availableProcessors());
        LOG.info("\tMemory: {}", FileSystem.readableFileSize(Runtime.getRuntime().totalMemory()));
    }

    private void shutdownLog(final double elapsedTime, final int frameNumber) {
        LOG.info("Terminating...");
        LOG.info("\tFrames: {}.", frameNumber);
        LOG.info("\tMilliseconds: {}.", elapsedTime);
        LOG.info("\tExit Code: {}.", getContext().getExitCode());

        log.shutdown();
    }

    private void handleExitRequest(final ExitRequested exitRequest) {
        shouldExit.set(true);
    }

    private void handleGLFWError(int error, long description) {
        LOG.error("GLFW Error {} {}", error, description);
        shouldExit.set(true);
        getContext().setExitCode(ExitCode.GLFW_RUNTIME_ERROR);
    }
}
