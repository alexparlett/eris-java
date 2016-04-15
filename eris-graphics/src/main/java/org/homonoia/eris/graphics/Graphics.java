package org.homonoia.eris.graphics;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.ExitCode;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.core.ExitRequested;
import org.homonoia.eris.events.graphics.ScreenMode;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Image;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glGetString;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 14/02/2016
 */
public class Graphics extends Contextual {

    private static final Logger LOG = LoggerFactory.getLogger(Graphics.class);

    private final ResourceCache resourceCache;

    private boolean initialized = false;
    private boolean fullscreen = true;
    private boolean resizable = false;
    private boolean borderless = false;
    private boolean vsync = true;
    private final AtomicInteger width = new AtomicInteger(0);
    private final AtomicInteger height = new AtomicInteger(0);
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
    public Graphics(final Context context, final ResourceCache resourceCache) {
        super(context);
        this.resourceCache = resourceCache;
    }

    public void initialize() throws InitializationException {
        if (isInitialized()) {
            return;
        }

        initializeRenderWindow();
        initalizeBackgroundWindow();

        try {
            setIconForGLFWWindow();
        } catch (IOException e) {
            throw new InitializationException("Failed to set Window Icons", ExitCode.GLFW_CREATE_ERROR, e);
        }

        initialized = true;
    }

    public void shutdown() {
        initialized = false;

        if (renderWindow != MemoryUtil.NULL) {
            glfwSetFramebufferSizeCallback(renderWindow, null);
            glfwSetWindowCloseCallback(renderWindow, null);

            glfwDestroyWindow(renderWindow);
            renderWindow = MemoryUtil.NULL;
        }

        if (backgroundWindow != MemoryUtil.NULL) {
            glfwSetWindowCloseCallback(backgroundWindow, null);

            glfwDestroyWindow(backgroundWindow);
            backgroundWindow = MemoryUtil.NULL;
        }
    }

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

    public void minimize() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwIconifyWindow(renderWindow);
        }
    }

    public void restore() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwRestoreWindow(renderWindow);
        }
    }

    public void hide() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwHideWindow(renderWindow);
        }
    }

    public void show() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwShowWindow(renderWindow);
        }
    }

    public void close() {
        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwSetWindowShouldClose(renderWindow, GLFW_TRUE);
        }

    }

    public void setSize(final int width, final int height) {
        this.width.set(width);
        this.height.set(height);

        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwSetWindowSize(renderWindow, width, height);
        }
    }

    public void setSamples(final int samples) {
        this.samples = samples;
    }

    public void setGamma(final float gamma) {
        this.gamma = gamma;

        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwSetGamma(glfwGetPrimaryMonitor(), gamma);
        }
    }

    public void setTitle(final String title) {
        this.title = title;

        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            glfwSetWindowTitle(renderWindow, title);
        }
    }

    public void setFullscreen(final boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public void setResizable(final boolean resizable) {
        this.resizable = resizable;
    }

    public void setBorderless(final boolean borderless) {
        this.borderless = borderless;
    }

    public void setVSync(final boolean vsync) {
        this.vsync = vsync;
    }

    public void setIcon(final String icon) {
        this.icon = icon;

        if (isInitialized() && renderWindow != MemoryUtil.NULL) {
            try {
                setIconForGLFWWindow();
            } catch (IOException e) {
                LOG.error("Failed to set Window Icons", e);
            }
        }
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public boolean isResizable() {
        return resizable;
    }

    public boolean isBorderless() {
        return borderless;
    }

    public boolean isVsync() {
        return vsync;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isFocused() {
        if (renderWindow != MemoryUtil.NULL) {
            return glfwGetWindowAttrib(renderWindow, GLFW_FOCUSED) == GLFW_TRUE;
        }
        return false;
    }

    public boolean isMinimized() {
        if (renderWindow != MemoryUtil.NULL) {
            return glfwGetWindowAttrib(renderWindow, GLFW_ICONIFIED) == GLFW_TRUE;
        }
        return false;
    }

    public boolean isVisible() {
        if (renderWindow != MemoryUtil.NULL) {
            return glfwGetWindowAttrib(renderWindow, GLFW_VISIBLE) == GLFW_TRUE;
        }
        return false;
    }

    public int getWidth() {
        return width.get();
    }

    public int getHeight() {
        return height.get();
    }

    public int getSamples() {
        return samples;
    }

    public float getGamma() {
        return gamma;
    }

    public String getTitle() {
        return title;
    }

    public long getRenderWindow() {
        return renderWindow;
    }

    public long getBackgroundWindow() {
        return backgroundWindow;
    }

    public String getIcon() {
        return icon;
    }

    public List<GLFWVidMode> getResolutions() {
        List<GLFWVidMode> videoModesList = Collections.emptyList();
        GLFWVidMode.Buffer vidModes = glfwGetVideoModes(glfwGetPrimaryMonitor());
        while (vidModes.hasRemaining()) {
            videoModesList.add(vidModes.get());
        }
        return videoModesList;
    }

    public GLFWVidMode getResolution() {
        return glfwGetVideoMode(glfwGetPrimaryMonitor());
    }

    private void initializeRenderWindow() throws InitializationException {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        glfwWindowHint(GLFW_DECORATED, borderless ? GLFW_FALSE : GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, samples);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        GLFWVidMode desktop = getResolution();
        if (width.get() <= 0 || height.get() <= 0) {
            width.set(desktop.width());
            height.set(desktop.height());
        }

        if (fullscreen) {
            renderWindow = glfwCreateWindow(width.get(), height.get(), title, glfwGetPrimaryMonitor(), MemoryUtil.NULL);
        } else {
            renderWindow = glfwCreateWindow(width.get(), height.get(), title, MemoryUtil.NULL, MemoryUtil.NULL);
        }

        if (renderWindow == MemoryUtil.NULL) {
            throw new InitializationException("Failed to open rendering context.", ExitCode.WINDOW_CREATE_ERROR);
        }

        glfwMakeContextCurrent(renderWindow);

        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        glfwGetFramebufferSize(renderWindow, widthBuffer, heightBuffer);
        this.width.set(widthBuffer.get());
        this.height.set(heightBuffer.get());

        if (isVsync()) {
            glfwSwapInterval(1);
        }

        try {
            GL.createCapabilities();
        } catch (IllegalStateException ex) {
            throw new InitializationException("Failed to create OpenGL capabilities.", ExitCode.GL_CREATE_ERROR);
        }

        if (!isFullscreen()) {
            glfwSetWindowPos(renderWindow, (desktop.width() - width.get()) / 2, (desktop.height() - height.get()) / 2);
        }

        glfwSetFramebufferSizeCallback(renderWindow, GLFWFramebufferSizeCallback.create(this::handleFramebufferCallback));
        glfwSetWindowCloseCallback(renderWindow, GLFWWindowCloseCallback.create(this::handleWindowCloseCallback));

        LOG.info("Initializing Graphics...");
        LOG.info("Adapter: {} {}", glGetString(GL11.GL_VENDOR), glGetString(GL11.GL_RENDERER));
        LOG.info("Open GL: {}", glGetString(GL11.GL_VERSION));
        LOG.info("GLSL: {}", glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
        LOG.info("GLFW: {}", glfwGetVersionString());

        glfwMakeContextCurrent(MemoryUtil.NULL);
    }

    private void initalizeBackgroundWindow() throws InitializationException {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        backgroundWindow = glfwCreateWindow(1, 1, title, MemoryUtil.NULL, renderWindow);

        if (backgroundWindow == MemoryUtil.NULL) {
            throw new InitializationException("Failed to open background loading context.", ExitCode.WINDOW_CREATE_ERROR);
        }

        glfwMakeContextCurrent(backgroundWindow);

        try {
            GL.createCapabilities();
        } catch (IllegalStateException ex) {
            throw new InitializationException("Failed to create OpenGL capabilities.", ExitCode.GL_CREATE_ERROR);
        }

        glfwSetWindowCloseCallback(backgroundWindow, GLFWWindowCloseCallback.create(this::handleWindowCloseCallback));

        glfwMakeContextCurrent(MemoryUtil.NULL);
    }

    private void setIconForGLFWWindow() throws IOException {
        if (icon == null || icon.isEmpty()) {
            return;
        }

        resourceCache.get(Image.class, Paths.get(icon)).ifPresent(image ->  {
            GLFWImage glfwImage = GLFWImage.create().set(image.getWidth(), image.getHeight(), image.getData());
            GLFWImage.Buffer buffer = GLFWImage.create(1).put(glfwImage);

            if (renderWindow != MemoryUtil.NULL) {
                glfwSetWindowIcon(renderWindow, buffer);
            }

            if (backgroundWindow != MemoryUtil.NULL) {
                glfwSetWindowIcon(backgroundWindow, buffer);
            }
        });
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
