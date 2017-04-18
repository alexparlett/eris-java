package org.homonoia.eris.engine;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.EmptyStatistics;
import org.homonoia.eris.core.ExitCode;
import org.homonoia.eris.core.FileSystem;
import org.homonoia.eris.core.Statistics;
import org.homonoia.eris.core.Timer;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.ecs.ComponentFactory;
import org.homonoia.eris.engine.properties.EngineProperties;
import org.homonoia.eris.events.core.ExitRequested;
import org.homonoia.eris.events.frame.Begin;
import org.homonoia.eris.events.frame.End;
import org.homonoia.eris.events.frame.Render;
import org.homonoia.eris.events.frame.Update;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.input.Input;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.json.JsonException;
import org.homonoia.eris.scripting.ScriptEngine;
import org.homonoia.eris.ui.UI;
import org.homonoia.eris.ui.elements.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_LEFT;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_TOP;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
public class Engine extends Contextual {

    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
    private AtomicBoolean shouldExit = new AtomicBoolean(false);
    private GLFWErrorCallback glfwErrorCallback;

    private EngineProperties engineProperties;
    private ResourceCache resourceCache;
    private Graphics graphics;

    private Renderer renderer;
    private Settings settings;
    private Locale locale;
    private FileSystem fileSystem;
    private Log log;
    private Input input;
    private UI ui;
    private Gson gson;
    private ScriptEngine scriptEngine;
    private Statistics statistics;

    private Text fps;
    private DecimalFormat decimalFormat = new DecimalFormat();

    /**
     * Instantiates a new Engine.
     *
     * @param context the context
     */
    public Engine(final Context context) {
        super(context);

        statistics = context.registerBean(context.isDebugEnabled() ? new Statistics() : new EmptyStatistics());

        context.registerBean(this);
        context.registerBean(Executors.newWorkStealingPool());
        context.registerBean(new ComponentFactory(context));

        gson = context.registerBean(new GsonBuilder()
                .setVersion(1.0)
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .create());

        log = new Log(context);
        fileSystem = new FileSystem(context);
        scriptEngine = new ScriptEngine(context);
        resourceCache = new ResourceCache(context, fileSystem);
        settings = new Settings(context, resourceCache, fileSystem);
        graphics = new Graphics(context, resourceCache);
        ui = new UI(context, resourceCache, graphics);
        input = new Input(context, graphics, ui);
        locale = new Locale(context, resourceCache);
        renderer = new Renderer(context, graphics, resourceCache, ui);

        subscribe(this::handleExitRequest, ExitRequested.class);
    }

    public void initialize() throws InitializationException {
        try (InputStreamReader isr = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("engine.json"))) {
            engineProperties = gson.fromJson(isr, EngineProperties.class);
        } catch (IOException e) {
            throw new InitializationException("Failed to load Engine Properties.", ExitCode.FATAL_ERROR, e);
        }

        // Initialize Base Components
        initializationLog();

        // Initialize Window
        glfwErrorCallback = GLFWErrorCallback.create(this::handleGLFWError);
        GLFW.glfwSetErrorCallback(glfwErrorCallback);
        if (!GLFW.glfwInit() || !getContext().getExitCode().equals(ExitCode.SUCCESS)) {
            throw new InitializationException("Failed to initialize GLFW.", ExitCode.GLFW_CREATE_ERROR);
        }

        scriptEngine.initialize();

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
        ui.initialize();

        decimalFormat.setGroupingUsed(false);
        decimalFormat.setDecimalSeparatorAlwaysShown(true);

        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setMaximumFractionDigits(2);

        fps = new Text(getContext());
        fps.setX(0);
        fps.setY(0);
        fps.setAlign(NK_TEXT_ALIGN_TOP| NK_TEXT_ALIGN_LEFT);
        fps.setFont(ui.getDefaultFont());
        ui.getRoot().getChildren().add(fps);
    }

    public void run() {
        double delta = 0.0;
        double rate = 1 / 60.0;

        Begin.Builder beginBuilder = Begin.builder();
        End.Builder endBuilder = End.builder();
        Update.Builder updateBuilder = Update.builder();
        Render.Builder renderBuilder = Render.builder();

        Timer timer = new Timer();

        graphics.show();
        try {
            while (!shouldExit.get()) {
                if (delta >= rate) {
                    fps.setText(decimalFormat.format(1 / delta));
                    statistics.beginFrame();
                    {
                        statistics.getCurrent().startSegment();
                        publish(beginBuilder.timeStep(delta));
                        statistics.getCurrent().endSegment("Begin");
                    }
                    {
                        statistics.getCurrent().startSegment();
                        publish(updateBuilder.timeStep(delta));
                        statistics.getCurrent().endSegment("Update");
                    }
                    {
                        statistics.getCurrent().startSegment();
                        publish(renderBuilder.timeStep(delta));
                        statistics.getCurrent().endSegment("Render");
                    }
                    {
                        statistics.getCurrent().startSegment();
                        publish(endBuilder.timeStep(delta));
                        statistics.getCurrent().endSegment("End");
                    }
                    statistics.endFrame(delta);
                    delta = timer.getElapsedTime(true);
                } else {
                    delta = timer.getElapsedTime(false);
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

        input.shutdown();
        renderer.shutdown();
        resourceCache.shutdown();
        ui.shutdown();
        graphics.shutdown();

        glfwErrorCallback.free();

        glfwTerminate();

        getContext().getBeans(Contextual.class).forEach(Contextual::destroy);

        shutdownLog();
    }

    private void initializationLog() {
        LOG.info("Initializing...");
        LOG.info("Arguments:");
        getContext().getCommandLineArgs().getOptionNames().forEach(option -> LOG.info("--{}", option));
        LOG.info("Version: {}", engineProperties.getVersion());
        LOG.info("OS: {}", System.getProperty("os.name"));
        LOG.info("Arch: {}", System.getProperty("os.arch"));
        LOG.info("Cores: {}", Runtime.getRuntime().availableProcessors());
        LOG.info("Memory: {}", FileSystem.readableFileSize(Runtime.getRuntime().totalMemory()));
    }

    private void shutdownLog() {
        LOG.info("Terminating...");
        LOG.info("Frames: {}", statistics.getTotalFrames());
        LOG.info("Seconds: {}", statistics.getElapsedTime());
        LOG.info("FPS: {}", statistics.getTotalFrames() / statistics.getElapsedTime());
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
