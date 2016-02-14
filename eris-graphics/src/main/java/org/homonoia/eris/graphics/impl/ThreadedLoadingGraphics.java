package org.homonoia.eris.graphics.impl;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.Errors;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.core.ExitRequested;
import org.homonoia.eris.events.graphics.ScreenMode;
import org.homonoia.eris.graphics.Graphics;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glGetString;

/**
 * Created by alexparlett on 14/02/2016.
 */
@ContextualComponent
public class ThreadedLoadingGraphics extends Contextual implements Graphics {

    private static final Logger LOG = LoggerFactory.getLogger(Graphics.class);

    private boolean initialized = false;
    private boolean fullscreen = true;
    private boolean resizable = false;
    private boolean borderless = false;
    private boolean vsync = true;
    private AtomicInteger width = new AtomicInteger(0);
    private AtomicInteger height = new AtomicInteger(0);
    private int samples = 4;
    private float gamma;
    private String title = "Eris";
    private long renderWindow = MemoryUtil.NULL;
    private long backgroundWindow = MemoryUtil.NULL;
    private String icon = "icon.ico";

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    @Autowired
    public ThreadedLoadingGraphics(final Context context) {
        super(context);
    }

    @Override
    public void initialize() throws InitializationException {
        if (isInitialized()) {
            return;
        }

        initalizeBackgroundWindow();
        initializeRenderWindow();

        initialized = true;
    }

    @Override
    public void terminate() {
        initialized = false;

        if (renderWindow != MemoryUtil.NULL) {
            glfwDestroyWindow(renderWindow);
            renderWindow = MemoryUtil.NULL;
        }

        if (backgroundWindow != MemoryUtil.NULL) {
            glfwDestroyWindow(backgroundWindow);
            backgroundWindow = MemoryUtil.NULL;
        }
    }

    @Override
    public void maximize() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            if (!isFullscreen()) {
                GLFWVidMode resolution = getResolution();
                glfwSetWindowSize(renderWindow, resolution.width(), resolution.height());
            } else {
                restore();
            }
        }
    }

    @Override
    public void minimize() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwIconifyWindow(renderWindow);
        }
    }

    @Override
    public void restore() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwRestoreWindow(renderWindow);
        }
    }

    @Override
    public void hide() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwHideWindow(renderWindow);
        }
    }

    @Override
    public void show() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwShowWindow(renderWindow);
        }
    }

    @Override
    public void close() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwSetWindowShouldClose(renderWindow, GLFW_TRUE);
        }

    }

    @Override
    public void setSize(final int width, final int height) {
        this.width.set(width);
        this.height.set(height);

        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwSetWindowSize(renderWindow, width, height);
        }
    }

    @Override
    public void setSamples(final int samples) {
        this.samples = samples;
    }

    @Override
    public void setGamma(final float gamma) {
        this.gamma = gamma;

        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwSetGamma(glfwGetPrimaryMonitor(), gamma);
        }
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;

        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwSetWindowTitle(renderWindow, title);
        }
    }

    @Override
    public void setFullscreen(final boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    @Override
    public void setResizable(final boolean resizable) {
        this.resizable = resizable;
    }

    @Override
    public void setBorderless(final boolean borderless) {
        this.borderless = borderless;
    }

    @Override
    public void setVSync(final boolean vsync) {
        this.vsync = vsync;
    }

    @Override
    public void setIcon(final String icon) {
        this.icon = icon;
    }

    @Override
    public boolean isFullscreen() {
        return fullscreen;
    }

    @Override
    public boolean isResizable() {
        return resizable;
    }

    @Override
    public boolean isBorderless() {
        return borderless;
    }

    @Override
    public boolean isVsync() {
        return vsync;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public int getWidth() {
        return width.get();
    }

    @Override
    public int getHeight() {
        return height.get();
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public float getGamma() {
        return gamma;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public long getRenderWindow() {
        return renderWindow;
    }

    @Override
    public long getBackgroundWindow() {
        return backgroundWindow;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public List<GLFWVidMode> getResolutions() {
        List<GLFWVidMode> videoModesList = Collections.emptyList();
        GLFWVidMode.Buffer vidModes = glfwGetVideoModes(glfwGetPrimaryMonitor()).asReadOnlyBuffer();
        while (vidModes.hasRemaining()) {
            videoModesList.add(vidModes.get());
        }
        return videoModesList;
    }

    @Override
    public GLFWVidMode getResolution() {
        return glfwGetVideoMode(glfwGetPrimaryMonitor());
    }

    private void initializeRenderWindow() throws InitializationException {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindowHint(GLFW_DECORATED, borderless ? GLFW_FALSE : GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, samples);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        GLFWVidMode desktop = getResolution();
        if (width.get() <= 0 || height.get() <= 0)
        {
            width.set(desktop.width());
            height.set(desktop.height());
        }

        if (fullscreen) {
            renderWindow = glfwCreateWindow(width.get(), height.get(), title, glfwGetPrimaryMonitor(), backgroundWindow);
        } else {
            renderWindow = glfwCreateWindow(width.get(), height.get(), title, MemoryUtil.NULL, backgroundWindow);
        }

        if (renderWindow == MemoryUtil.NULL)
        {
            throw new InitializationException("", Errors.WINDOW_CREATE_ERROR);
        }

        glfwMakeContextCurrent(renderWindow);

        IntBuffer buffer = BufferUtils.createIntBuffer(2);
        glfwGetFramebufferSize(renderWindow, buffer, buffer);
        this.width.set(buffer.get());
        this.height.set(buffer.get());

        if (isVsync()) {
            glfwSwapInterval(1);
        }

        try {
            GL.createCapabilities();
        } catch (IllegalStateException ex) {
            throw new InitializationException("", Errors.GL_CREATE_ERROR);
        }

        if (!isFullscreen()) {
            glfwSetWindowPos(renderWindow, (desktop.width() - width.get()) / 2, (desktop.height() - height.get()) / 2);
        }

        glfwSetFramebufferSizeCallback(renderWindow, GLFWFramebufferSizeCallback.create(this::handleFramebufferCallback));
        glfwSetWindowCloseCallback(renderWindow, GLFWWindowCloseCallback.create(this::handleWindowCloseCallback));

        LOG.info("Initializing Graphics...");
        LOG.info("\tAdapter: %s %s", glGetString(GL11.GL_VENDOR), glGetString(GL11.GL_RENDERER));
        LOG.info("\tOpen GL: %s", glGetString(GL11.GL_VERSION));
        LOG.info("\tGLSL: %s", glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
        LOG.info("\tGLFW: %s", glfwGetVersionString());

        show();

        glfwMakeContextCurrent(MemoryUtil.NULL);
    }

    private void initalizeBackgroundWindow() throws InitializationException {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        backgroundWindow = glfwCreateWindow(1, 1, title, MemoryUtil.NULL, MemoryUtil.NULL);

        if (backgroundWindow == MemoryUtil.NULL)
        {
            throw new InitializationException("", Errors.WINDOW_CREATE_ERROR);
        }

        glfwMakeContextCurrent(backgroundWindow);

        try {
            GL.createCapabilities();
        } catch (IllegalStateException ex) {
            throw new InitializationException("", Errors.GL_CREATE_ERROR);
        }

        glfwSetWindowCloseCallback(backgroundWindow, GLFWWindowCloseCallback.create(this::handleWindowCloseCallback));

        glfwMakeContextCurrent(MemoryUtil.NULL);
    }

    private void handleFramebufferCallback(final long window, final int width, final int height) {
        this.width.set(width);
        this.height.set(height);

        publish(ScreenMode.builder()
                .width(width)
                .height(height));
    }

    private void handleWindowCloseCallback(final long window) {
        publish(ExitRequested.builder());
    }
}
