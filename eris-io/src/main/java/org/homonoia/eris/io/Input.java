package org.homonoia.eris.io;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.graphics.Graphics;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryUtil;

/**
 * Created by alexp on 01/03/2016.
 */
@ContextualComponent
public class Input extends Contextual {

    private final Graphics graphics;
    private boolean initialized = false;

    public Input(final Context context, final Graphics graphics) {
        super(context);
        this.graphics = graphics;
    }

    public void initialize() throws InitializationException {
        if (!initialized) {
            long renderWindow = graphics.getRenderWindow();
            if (renderWindow == MemoryUtil.NULL) {
                throw new InitializationException("Cannot initialize input before graphics, window is not defined yet.");
            }

            GLFW.glfwSetKeyCallback(renderWindow, GLFWKeyCallback.create(this::handleGLFWKeyCallback));
            GLFW.glfwSetMouseButtonCallback(renderWindow, GLFWMouseButtonCallback.create(this::handleGLFWMouseButtonCallback));
            GLFW.glfwSetCursorPosCallback(renderWindow, GLFWCursorPosCallback.create(this::handleGLFWCursorPosCallback));
            GLFW.glfwSetScrollCallback(renderWindow, GLFWScrollCallback.create(this::handleGLFWScrollCallback));

            initialized = true;
        }
    }

    public void update() {

    }

    public void reset() {

    }

    public void shutdown() {

    }


    public boolean isKeyDown(int key) {
        return false;
    }

    public boolean isKeyPressed(int key) {
        return false;
    }

    public boolean isScancodeDown(int scancode) {
        return false;
    }

    public boolean isScancodePressed(int scancode) {
        return false;
    }

    public boolean isModifierDown(int modifier) {
        return false;
    }

    public boolean isModifierPressed(int modifier) {
        return false;
    }

    public boolean isMouseButtonDown(int button) {
        return false;
    }

    public boolean isMouseButtonPressed(int button) {
        return false;
    }

    public int getModifiersDown() {
        return 0;
    }

    private void handleGLFWKeyCallback(long window, int key, int scancode, int action, int mods) {

    }

    private void handleGLFWMouseButtonCallback(long window, int button, int action, int mods) {

    }

    private void handleGLFWCursorPosCallback(long window, double xpos, double ypos) {

    }

    private void handleGLFWScrollCallback(long window, double xoffset, double yoffset) {

    }
}
