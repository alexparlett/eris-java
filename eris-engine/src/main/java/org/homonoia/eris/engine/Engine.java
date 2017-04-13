package org.homonoia.eris.engine;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.ExitCode;
import org.homonoia.eris.core.components.Clock;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.core.utils.Timer;
import org.homonoia.eris.engine.properties.EngineProperties;
import org.homonoia.eris.events.core.ExitRequested;
import org.homonoia.eris.events.frame.Update;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.input.Input;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.json.JsonException;
import org.homonoia.eris.scripting.ScriptBinding;
import org.homonoia.eris.scripting.ScriptEngine;
import org.homonoia.eris.ui.UI;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
public class Engine extends Contextual implements ScriptBinding {

    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private AtomicBoolean shouldExit = new AtomicBoolean(false);
    private GLFWErrorCallback glfwErrorCallback;
    private EngineProperties engineProperties;

    private ResourceCache resourceCache;
    private Graphics graphics;
    private Clock clock;
    private Renderer renderer;
    private Settings settings;
    private Locale locale;
    private FileSystem fileSystem;
    private Log log;
    private Input input;
    private ScriptEngine scriptEngine;
    private UI ui;
    private Gson gson;

    /**
     * Instantiates a new Engine.
     *
     * @param context the context
     */
    public Engine(final Context context) {
        super(context);

        context.registerBean(Executors.newWorkStealingPool());

        gson = context.registerBean(new GsonBuilder()
                .setVersion(1.0)
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .create());

        log = new Log(context);
        clock = new Clock(context);
        fileSystem = new FileSystem(context);
        resourceCache = new ResourceCache(context, fileSystem);
        graphics = new Graphics(context, resourceCache);
        settings = new Settings(context, resourceCache, fileSystem);
        input = new Input(context, graphics);
        locale = new Locale(context, resourceCache);
        scriptEngine = new ScriptEngine(context);
        ui = new UI(context);
        renderer = new Renderer(context, graphics, resourceCache);

        subscribe(this::handleExitRequest, ExitRequested.class);
    }

    @Override
    public void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindClass(Timer.class);
        scriptEngine.bindGlobal("clock", clock);
    }

    public void initialize() throws InitializationException {
        try (InputStreamReader isr = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("engine.json"))) {
            engineProperties = gson.fromJson(isr, EngineProperties.class);
        } catch (IOException e) {
            throw new InitializationException("Failed to load Engine Properties.", ExitCode.FATAL_ERROR, e);
        }

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
            locale.load(settings.getString("Game", "Language").orElse("enGB"));
        } catch (IOException | JsonException e) {
            throw new InitializationException("Failed to load Locale.", ExitCode.FATAL_ERROR, e);
        }

        // Initialize Window
        glfwErrorCallback = GLFWErrorCallback.create(this::handleGLFWError);
        GLFW.glfwSetErrorCallback(glfwErrorCallback);
        if (!GLFW.glfwInit() || !getContext().getExitCode().equals(ExitCode.SUCCESS)) {
            throw new InitializationException("Failed to initialize GLFW.", ExitCode.GLFW_CREATE_ERROR);
        }

        graphics.setTitle(settings.getString("Game", "Title").orElse("Eris"));
        graphics.setIcon(settings.getString("Game", "Icon").orElse(null));
        graphics.setResizable(settings.getBoolean("Graphics", "Resizable").orElse(false));
        graphics.setSize(settings.getInteger("Graphics", "Width").orElse(0), settings.getInteger("Graphics", "Height").orElse(0));
        graphics.setBorderless(settings.getBoolean("Graphics", "Borderless").orElse(false));
        graphics.setFullscreen(settings.getBoolean("Graphics", "Fullscreen").orElse(true));
        graphics.setVSync(settings.getBoolean("Graphics", "VSync").orElse(true));
        graphics.setSamples(settings.getInteger("Graphics", "Multisamples").orElse(4));
        graphics.setGamma(settings.getFloat("Graphics", "Gamma").orElse(1.f));

        graphics.initialize();

        try {
            renderer.initialize();
        } catch (Exception e) {
            throw new InitializationException("Failed to initialize Renderer", ExitCode.FATAL_ERROR, e);
        }

        input.initialize();
        scriptEngine.initialize();
        ui.initialize();
    }

    public void run() {
        double delta = 0.0;
        double rate = 1000.0 / 60.0;

        Update.Builder updateBuilder = Update.builder();
        Timer timer = new Timer();

        graphics.show();
        try {
            while (!shouldExit.get()) {
                delta += timer.getElapsedTime(true);
                if (delta >= rate) {
                    clock.beginFrame(delta);
                    publish(updateBuilder.timeStep(delta));
                    clock.endFrame();
                    delta -= rate;
                }
            }
        } catch (Throwable t) {
            LOG.error("Unhandled exception", t);
            getContext().setExitCode(ExitCode.RUNTIME);
            shouldExit.set(true);
        }
    }

    public void shutdown() {
        graphics.hide();

        scriptEngine.shutdown();
        input.shutdown();
        renderer.shutdown();
        resourceCache.shutdown();
        graphics.shutdown();

        double elapsedTime = clock.getElapsedTime();
        int frameNumber = clock.getFrameNumber();

        glfwErrorCallback.free();

        GLFW.glfwTerminate();

        shutdownLog(elapsedTime, frameNumber);
    }

    private void initializationLog() {
        LOG.info("Initializing...");
        LOG.info("Version: {}", engineProperties.getVersion());
        LOG.info("OS: {}", System.getProperty("os.name"));
        LOG.info("Arch: {}", System.getProperty("os.arch"));
        LOG.info("Cores: {}", Runtime.getRuntime().availableProcessors());
        LOG.info("Memory: {}", FileSystem.readableFileSize(Runtime.getRuntime().totalMemory()));
    }

    private void shutdownLog(final double elapsedTime, final int frameNumber) {
        LOG.info("Terminating...");
        LOG.info("Frames: {}", frameNumber);
        LOG.info("Milliseconds: {}", elapsedTime);
        LOG.info("FPS: {}", frameNumber / (elapsedTime / 1000));
        LOG.info("Exit Code: {}", getContext().getExitCode());
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
