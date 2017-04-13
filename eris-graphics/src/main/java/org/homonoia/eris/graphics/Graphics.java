package org.homonoia.eris.graphics;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.ExitCode;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.core.ExitRequested;
import org.homonoia.eris.events.graphics.ScreenMode;
import org.homonoia.eris.graphics.drawables.RenderTarget;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Image;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUSED;
import static org.lwjgl.glfw.GLFW.GLFW_ICONIFIED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwRestoreWindow;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetGamma;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.system.MemoryStack.stackPush;

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
    private int samples = 4;
    private float gamma;
    private String title = "Eris";
    private long renderWindow = MemoryUtil.NULL;
    private long backgroundWindow = MemoryUtil.NULL;
    private String icon = "icon.ico";
    private RenderTarget defaultRenderTarget = new RenderTarget();
    private int width;
    private int height;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
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
            glfwSetWindowShouldClose(renderWindow, true);
        }

    }

    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;

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
        return width;
    }

    public int getHeight() {
        return height;
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

    public RenderTarget getDefaultRenderTarget() {
        return defaultRenderTarget;
    }

    public List<GLFWVidMode> getResolutions() {
        List<GLFWVidMode> videoModesList = new ArrayList<>();
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
        glfwWindowHint(GLFW_RESIZABLE, resizable && !fullscreen ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, samples);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        GLFWVidMode desktop = getResolution();
        if (getWidth() <= 0 || getHeight() <= 0 || borderless) {
            width = desktop.width();
            height = desktop.height();
        }

        if (fullscreen && !borderless) {
            renderWindow = glfwCreateWindow(getWidth(), getHeight(), title, glfwGetPrimaryMonitor(), MemoryUtil.NULL);
        } else {
            renderWindow = glfwCreateWindow(getWidth(), getHeight(), title, MemoryUtil.NULL, MemoryUtil.NULL);
        }

        if (renderWindow == MemoryUtil.NULL) {
            throw new InitializationException("Failed to open rendering context.", ExitCode.WINDOW_CREATE_ERROR);
        }

        glfwMakeContextCurrent(renderWindow);

        if (!isFullscreen()) {
            glfwSetWindowPos(renderWindow, (desktop.width() - getWidth()) / 2, (desktop.height() - getHeight()) / 2);
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            glfwGetFramebufferSize(renderWindow, widthBuffer, heightBuffer);
            this.defaultRenderTarget.width(widthBuffer.get()).height(heightBuffer.get());
        }

        if (isVsync()) {
            glfwSwapInterval(1);
        }

        try {
            GL.createCapabilities();
        } catch (IllegalStateException ex) {
            throw new InitializationException("Failed to create OpenGL capabilities.", ExitCode.GL_CREATE_ERROR);
        }

        glfwSetWindowSizeCallback(renderWindow, GLFWWindowSizeCallback.create(this::handleWindowSizeCallback));
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
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

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
        LOG.debug("framebuffer size changed w: {} h: {}", width, height);

        this.defaultRenderTarget.width(width)
            .height(height);

        publish(ScreenMode.builder()
                .width(width)
                .height(height));
    }

    private void handleWindowCloseCallback(final long window) {
        publish(ExitRequested.builder());
    }

    private void handleWindowSizeCallback(long window, int width, int height) {
        LOG.debug("window size changed w: {} h: {}", width, height);

        this.width = width;
        this.height = height;
    }
}
