package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.frame.Render;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.math.Vector2b;
import org.homonoia.eris.math.Vector3b;
import org.homonoia.eris.math.Vector4b;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.RenderState;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.renderer.commands.ClearColorCommand;
import org.homonoia.eris.renderer.commands.ClearCommand;
import org.homonoia.eris.renderer.commands.EnableCommand;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

/**
 * Created by alexparlett on 06/02/2016.
 */
@ContextualComponent
public class ThreadedRenderer extends Contextual implements Renderer, Runnable {

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
    private AtomicBoolean threadExit = new AtomicBoolean(false);
    private AtomicBoolean viewportDirty = new AtomicBoolean(false);
    private Matrix4f view = new Matrix4f();
    private Matrix4f perspective = new Matrix4f();
    private Thread thread = new Thread(this);
    private RenderState state = new SwappingRenderState(this);

    @Autowired
    public ThreadedRenderer(final Context context, final Graphics graphics) {
        super(context);
        this.graphics = graphics;

        subscribe(this::handleRenderEvent, Render.class);
    }

    @Override
    public void initialize() {
        if (initialized) {
            return;
        }

        thread.start();
    }

    @Override
    public void run() {

        long window = graphics.getWindow();

        try {
            initializeOpenGl(window, graphics.getWidth(), graphics.getHeight());
        } catch (InitializationException e) {
            return;
        }

        while (!threadExit.get()) {
            state.process();
            glfwSwapBuffers(0);

            if (viewportDirty.get()) {
                glViewport(0, 0, graphics.getWidth(), graphics.getHeight());
                glfwSwapBuffers(0);
            }
        }

        glfwMakeContextCurrent(MemoryUtil.NULL);
    }

    @Override
    public void terminate() {
        initialized = false;
        threadExit.set(true);

        try {
            thread.join();
        } catch (InterruptedException e) {

        }
    }

    @Override
    public Matrix4f getCurrentView() {
        return view;
    }

    @Override
    public void setCurrentView(final Matrix4f view) {
        assert (view != null);
        this.view = view;
    }

    @Override
    public Matrix4f getCurrentPerspective() {
        return perspective;
    }

    @Override
    public void setCurrentPerspective(final Matrix4f perspective) {
        assert (perspective != null);
        this.perspective = perspective;
    }

    @Override
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

    private void initializeOpenGl(final long window, final int width, final int height) throws InitializationException {

        if (window != MemoryUtil.NULL) {
            glfwMakeContextCurrent(window);
        } else {
            throw new InitializationException("Failed to initialize Renderer.\nAttempting to initialize OpenGL without a Window.");
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
}