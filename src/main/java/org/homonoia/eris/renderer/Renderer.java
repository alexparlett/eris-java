package org.homonoia.eris.renderer;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.ExitCode;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.frame.Render;
import org.homonoia.eris.events.graphics.ScreenMode;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.Model;
import org.homonoia.eris.renderer.impl.SwappingRenderState;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.ui.UI;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class Renderer extends Contextual {

    private static final Logger LOG = LoggerFactory.getLogger(Renderer.class);

    private final Graphics graphics;
    private final ResourceCache resourceCache;

    private boolean initialized = false;
    private final AtomicBoolean viewportDirty = new AtomicBoolean(false);
    private Matrix4f view = new Matrix4f();
    private Matrix4f projection = new Matrix4f();
    private final RenderState state;
    private DebugMode debugMode = new DebugMode();

    public Renderer(final Context context, final Graphics graphics, final ResourceCache resourceCache, final UI ui) {
        super(context);
        context.registerBean(this);
        this.graphics = graphics;
        this.resourceCache = resourceCache;
        this.state = new SwappingRenderState(this, ui);
    }

    public void initialize() throws Exception {
        if (initialized) {
            return;
        }

        subscribe(this::handleScreenMode, ScreenMode.class);
        subscribe(this::handleRenderEvent, Render.class);

        configureDebugMode();

        initializeOpenGl();
    }

    public void shutdown() {
        initialized = false;

        unsubscribe();

        LOG.info("Terminating Renderer...");
    }

    public Matrix4f getCurrentView() {
        return view;
    }

    public void setCurrentView(final Matrix4f view) {
        assert (view != null);
        this.view = view;
    }

    public Matrix4f getCurrentProjection() {
        return projection;
    }

    public void setCurrentProjection(final Matrix4f projection) {
        assert (projection != null);
        this.projection = projection;
    }

    public RenderState getState() {
        return state;
    }

    public DebugMode getDebugMode() {
        return debugMode;
    }

    private void configureDebugMode() throws Exception {
        Model cube = resourceCache.get(Model.class, "Models/bounding.mdl").orElseThrow(() -> new InitializationException("Debug Model not available"));
        debugMode.setBoundingBoxCube(cube);

        debugMode.setAxis(getContext().isDebugEnabled());
        debugMode.setBoundingBoxes(getContext().isDebugEnabled());
        debugMode.setGrid(getContext().isDebugEnabled());
    }

    private void initializeOpenGl() throws InitializationException {
        try {
            GL.createCapabilities();
        } catch (IllegalStateException ex) {
            throw new InitializationException("Failed to initialize Renderer.\nFailed to create OpenGL capabilities.", ExitCode.GL_CREATE_ERROR);
        }

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_TEXTURE_CUBE_MAP);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        glEnable(GL_DEPTH_TEST);

        glDepthFunc(GL_LESS);

        glViewport(0, 0, graphics.getDefaultRenderTarget().getWidth(), graphics.getDefaultRenderTarget().getHeight());
    }

    private void handleScreenMode(final ScreenMode evt) {
        viewportDirty.set(true);
    }

    private void handleRenderingThreadException(final Thread thread, final Throwable throwable) {
        LOG.error("Uncaught Exception in Rendering Thread", throwable);
        getContext().setExitCode(ExitCode.RUNTIME);
        glfwSetWindowShouldClose(graphics.getRenderWindow(), true);
    }

    private void handleRenderEvent(final Render evt) {
        long window = graphics.getRenderWindow();

        state.process();
        glfwSwapBuffers(window);

        if (viewportDirty.get()) {
            glViewport(0, 0, graphics.getDefaultRenderTarget().getWidth(), graphics.getDefaultRenderTarget().getHeight());
            glfwSwapBuffers(window);
            viewportDirty.set(false);
        }
    }
}
