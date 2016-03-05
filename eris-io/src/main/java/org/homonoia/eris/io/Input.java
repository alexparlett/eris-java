package org.homonoia.eris.io;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.io.*;
import org.homonoia.eris.graphics.Graphics;
import org.joml.Vector2d;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryUtil;

/**
 * Created by alexp on 01/03/2016.
 */
@ContextualComponent
public class Input extends Contextual {

    private static long renderWindow = MemoryUtil.NULL;

    private final Graphics graphics;

    private boolean initialized = false;
    private Vector2d mouseLastPosition = new Vector2d();

    public Input(final Context context, final Graphics graphics) {
        super(context);
        this.graphics = graphics;
    }

    public void initialize() throws InitializationException {
        if (!initialized) {
            renderWindow = graphics.getRenderWindow();
            if (renderWindow == MemoryUtil.NULL) {
                throw new InitializationException("Cannot initialize input before graphics, window is not defined yet.");
            }

            GLFW.glfwSetInputMode(renderWindow, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            GLFW.glfwSetInputMode(renderWindow, GLFW.GLFW_STICKY_KEYS, GLFW.GLFW_FALSE);
            GLFW.glfwSetInputMode(renderWindow, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GLFW.GLFW_FALSE);

            GLFW.glfwSetKeyCallback(renderWindow, GLFWKeyCallback.create(this::handleGLFWKeyCallback));
            GLFW.glfwSetMouseButtonCallback(renderWindow, GLFWMouseButtonCallback.create(this::handleGLFWMouseButtonCallback));
            GLFW.glfwSetCursorPosCallback(renderWindow, GLFWCursorPosCallback.create(this::handleGLFWCursorPosCallback));
            GLFW.glfwSetScrollCallback(renderWindow, GLFWScrollCallback.create(this::handleGLFWScrollCallback));

            initialized = true;
        }
    }

    public void update() {
        GLFW.glfwPollEvents();
    }

    public void shutdown() {
        if (initialized) {
            long renderWindow = graphics.getRenderWindow();

            GLFW.glfwSetKeyCallback(renderWindow, null);
            GLFW.glfwSetMouseButtonCallback(renderWindow, null);
            GLFW.glfwSetCursorPosCallback(renderWindow, null);
            GLFW.glfwSetScrollCallback(renderWindow, null);

            initialized = false;
        }
    }

    public static boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(renderWindow, key) == GLFW.GLFW_PRESS;
    }

    public static boolean isMouseButtonDown(int button) {
        return GLFW.glfwGetMouseButton(renderWindow, button) == GLFW.GLFW_PRESS;
    }

    private void handleGLFWKeyCallback(long window, int key, int scancode, int action, int mods) {
        if (window == graphics.getRenderWindow()) {
            if (action == GLFW.GLFW_PRESS) {
                publish(KeyDown.builder()
                        .key(key)
                        .scancode(scancode)
                        .mods(mods)
                        .repeat(false));
            } else if (action == GLFW.GLFW_REPEAT) {
                publish(KeyDown.builder()
                        .key(key)
                        .scancode(scancode)
                        .mods(mods)
                        .repeat(true));
            } else {
                publish(KeyUp.builder()
                        .key(key)
                        .scancode(scancode)
                        .mods(mods)
                        .repeat(true));
            }
        }
    }

    private void handleGLFWMouseButtonCallback(long window, int button, int action, int mods) {
        if (window == graphics.getRenderWindow()) {
            if (action == GLFW.GLFW_PRESS) {
                publish(MouseButtonDown.builder()
                        .button(button)
                        .mods(mods));
            } else {
                publish(MouseButtonUp.builder()
                        .button(button)
                        .mods(mods));
            }
        }
    }

    private void handleGLFWCursorPosCallback(long window, double xpos, double ypos) {
        if (window == graphics.getRenderWindow()) {
            Vector2d position = new Vector2d(xpos, ypos);
            Vector2d delta = new Vector2d(mouseLastPosition).sub(position);

            mouseLastPosition = position;

            publish(MouseMove.builder()
                    .position(position)
                    .delta(delta));
        }
    }

    private void handleGLFWScrollCallback(long window, double xoffset, double yoffset) {
        if (window == graphics.getRenderWindow()) {
            double delta = xoffset > yoffset ? xoffset : -yoffset;

            publish(MouseScroll.builder()
                    .delta(delta));
        }
    }
}
