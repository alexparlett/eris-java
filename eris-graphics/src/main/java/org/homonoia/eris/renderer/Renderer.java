package org.homonoia.eris.renderer;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.ExitCode;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.graphics.Render;
import org.homonoia.eris.events.graphics.ScreenMode;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.math.*;
import org.homonoia.eris.renderer.commands.ClearColorCommand;
import org.homonoia.eris.renderer.commands.ClearCommand;
import org.homonoia.eris.renderer.commands.EnableCommand;
import org.homonoia.eris.renderer.impl.SwappingRenderState;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL40.*;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class Renderer extends Contextual implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Render.class);

    private static final EnableCommand ENABLE_CULL_COMMAND = EnableCommand.builder()
            .renderKey(RenderKey.builder()
                    .command(1)
                    .depth(0)
                    .extra(0)
                    .material(0)
                    .target(0)
                    .targetLayer(1)
                    .transparency(0)
                    .build())
            .capability(GL_CULL_FACE)
            .build();

    private static final EnableCommand ENABLE_DEPTH_COMMAND = EnableCommand.builder()
            .renderKey(RenderKey.builder()
                    .command(1)
                    .depth(0)
                    .extra(0)
                    .material(0)
                    .target(0)
                    .targetLayer(1)
                    .transparency(0)
                    .build())
            .capability(GL_DEPTH_TEST)
            .build();

    private static final ClearCommand CLEAR_COMMAND = ClearCommand.builder()
            .renderKey(RenderKey.builder()
                    .command(0)
                    .depth(0)
                    .extra(0)
                    .material(0)
                    .target(0)
                    .targetLayer(0)
                    .transparency(0)
                    .build())
            .bitfield(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
            .build();

    private static final ClearColorCommand CLEAR_COLOR_COMMAND = ClearColorCommand.builder()
            .renderKey(RenderKey.builder()
                    .command(0)
                    .depth(0)
                    .extra(0)
                    .material(0)
                    .target(0)
                    .targetLayer(0)
                    .transparency(0)
                    .build())
            .color(new Vector4f(0.f, 0.f, 0.f, 1.f))
            .build();

    private final Graphics graphics;

    private boolean initialized = false;
    private final AtomicBoolean threadExit = new AtomicBoolean(false);
    private final AtomicBoolean viewportDirty = new AtomicBoolean(false);
    private Matrix4f view = new Matrix4f();
    private Matrix4f perspective = new Matrix4f();
    private final Thread thread = new Thread(this);
    private final RenderState state = new SwappingRenderState(this);

    @Autowired
    public Renderer(final Context context, final Graphics graphics) {
        super(context);
        this.graphics = graphics;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        subscribe(this::handleRenderEvent, Render.class);
        subscribe(this::handleScreenMode, ScreenMode.class);

        thread.setUncaughtExceptionHandler(this::handleRenderingThreadException);
        thread.start();
    }

    @Override
    public void run() {

        long window = graphics.getRenderWindow();

        try {
            initializeOpenGl(window, graphics.getWidth(), graphics.getHeight());
        } catch (InitializationException e) {
            LOG.error("Initialization Error in Renderer", e);
            getContext().setExitCode(e.getError());
            glfwSetWindowShouldClose(graphics.getRenderWindow(), true);
        }

        while (!threadExit.get()) {
            state.process();
            glfwSwapBuffers(window);

            if (viewportDirty.get()) {
                glViewport(0, 0, graphics.getWidth(), graphics.getHeight());
                glfwSwapBuffers(window);
            }
        }

        glfwMakeContextCurrent(MemoryUtil.NULL);
    }

    public void shutdown() {
        initialized = false;
        threadExit.set(true);

        unsubscribe();

        try {
            thread.join();
        } catch (InterruptedException e) {

        }
    }

    public Matrix4f getCurrentView() {
        return view;
    }

    public void setCurrentView(final Matrix4f view) {
        assert (view != null);
        this.view = view;
    }

    public Matrix4f getCurrentPerspective() {
        return perspective;
    }

    public void setCurrentPerspective(final Matrix4f perspective) {
        assert (perspective != null);
        this.perspective = perspective;
    }

    public void bindUniform(final int location, final int type, final java.lang.Object data) {
        switch (type) {
            case GL_FLOAT:
                Float fData = (Float) data;
                glUniform1f(location, fData);
                break;
            case GL_FLOAT_VEC2:
                Vector2f v2fData = (Vector2f) data;
                glUniform2f(location, v2fData.x, v2fData.y);
                break;
            case GL_FLOAT_VEC3:
                Vector3f v3fData = (Vector3f) data;
                glUniform3f(location, v3fData.x, v3fData.y, v3fData.z);
                break;
            case GL_FLOAT_VEC4:
                Vector4f v4fData = (Vector4f) data;
                glUniform4f(location, v4fData.x, v4fData.y, v4fData.z, v4fData.w);
                break;
            case GL_DOUBLE:
                Double dData = (Double) data;
                glUniform1d(location, dData);
                break;
            case GL_DOUBLE_VEC2:
                Vector2d v2dData = (Vector2d) data;
                glUniform2d(location, v2dData.x, v2dData.y);
                break;
            case GL_DOUBLE_VEC3:
                Vector3d v3dData = (Vector3d) data;
                glUniform3d(location, v3dData.x, v3dData.y, v3dData.z);
                break;
            case GL_DOUBLE_VEC4:
                Vector4d v4dData = (Vector4d) data;
                glUniform4d(location, v4dData.x, v4dData.y, v4dData.z, v4dData.w);
                break;
            case GL_INT:
                Integer iData = (Integer) data;
                glUniform1i(location, iData);
                break;
            case GL_INT_VEC2:
                Vector2i v2iData = (Vector2i) data;
                glUniform2i(location, v2iData.x, v2iData.y);
                break;
            case GL_INT_VEC3:
                Vector3i v3iData = (Vector3i) data;
                glUniform3i(location, v3iData.x, v3iData.y, v3iData.z);
                break;
            case GL_INT_VEC4:
                Vector4i v4iData = (Vector4i) data;
                glUniform4i(location, v4iData.x, v4iData.y, v4iData.z, v4iData.w);
                break;
            case GL_BOOL:
                Boolean bData = (Boolean) data;
                glUniform1i(location, bData ? 1 : 0);
                break;
            case GL_BOOL_VEC2:
                Vector2b v2bData = (Vector2b) data;
                glUniform2i(location, v2bData.x ? 1 : 0, v2bData.y ? 1 : 0);
                break;
            case GL_BOOL_VEC3:
                Vector3b v3bData = (Vector3b) data;
                glUniform3i(location, v3bData.x ? 1 : 0, v3bData.y ? 1 : 0, v3bData.z ? 1 : 0);
                break;
            case GL_BOOL_VEC4:
                Vector4b v4bData = (Vector4b) data;
                glUniform4i(location, v4bData.x ? 1 : 0, v4bData.y ? 1 : 0, v4bData.z ? 1 : 0, v4bData.w ? 1 : 0);
                break;
            case GL_FLOAT_MAT3:
                Matrix3f m3f = (Matrix3f) data;
                FloatBuffer m3fb = m3f.get(BufferUtils.createFloatBuffer(9));
                glUniformMatrix3fv(location, false, m3fb);
                break;
            case GL_FLOAT_MAT4:
                Matrix4f m4f = (Matrix4f) data;
                FloatBuffer m4fb = m4f.get(BufferUtils.createFloatBuffer(16));
                glUniformMatrix4fv(location, false, m4fb);
                break;
        }
    }

    public RenderState getState() {
        return state;
    }

    private void initializeOpenGl(final long window, final int width, final int height) throws InitializationException {

        if (window != MemoryUtil.NULL) {
            glfwMakeContextCurrent(window);
        } else {
            throw new InitializationException("Failed to initialize Renderer.\nAttempting to initialize OpenGL without a Window.", ExitCode.GL_CREATE_ERROR);
        }

        try {
            GL.createCapabilities();
        } catch (IllegalStateException ex) {
            throw new InitializationException("Failed to initialize Renderer.\nFailed to create OpenGL capabilities.", ExitCode.GL_CREATE_ERROR);
        }

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_TEXTURE_CUBE_MAP);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

        glViewport(0, 0, width, height);
    }

    private void handleRenderEvent(final Render evt) {
        state.add(CLEAR_COLOR_COMMAND);
        state.add(CLEAR_COMMAND);
        state.add(ENABLE_DEPTH_COMMAND);
        state.add(ENABLE_CULL_COMMAND);
    }

    private void handleScreenMode(final ScreenMode evt) {
        viewportDirty.set(true);
    }

    private void handleRenderingThreadException(final Thread thread, final Throwable throwable) {
        LOG.error("Uncaught Exception in Rendering Thread", throwable);
        getContext().setExitCode(ExitCode.RUNTIME);
        glfwSetWindowShouldClose(graphics.getRenderWindow(), true);
    }
}
