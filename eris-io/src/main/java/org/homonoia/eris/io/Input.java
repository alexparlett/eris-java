package org.homonoia.eris.io;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.frame.BeginFrame;
import org.homonoia.eris.graphics.Graphics;
import org.joml.Vector2d;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexp on 01/03/2016.
 */
@ContextualComponent
public class Input extends Contextual {

    private final Graphics graphics;

    private boolean initialized = false;
    private int mouseButtonsDown = 0;
    private int mouseButtonsPressed = 0;
    private double mouseWheelMoved = 0;
    private Vector2d mouseMoved = new Vector2d();
    private Vector2d mouseLastPosition = new Vector2d();
    private Set<Integer> keyDown = new HashSet<>();
    private Set<Integer> keyPressed = new HashSet<>();
    private Set<Integer> scanCodeDown = new HashSet<>();
    private Set<Integer> scanCodePressed = new HashSet<>();

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

            subscribe(this::handleBeginFrame, BeginFrame.class);

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

    }

    public void reset() {

    }

    public void shutdown() {
        if (initialized) {
            long renderWindow = graphics.getRenderWindow();

            GLFW.glfwSetKeyCallback(renderWindow, null);
            GLFW.glfwSetMouseButtonCallback(renderWindow, null);
            GLFW.glfwSetCursorPosCallback(renderWindow, null);
            GLFW.glfwSetScrollCallback(renderWindow, null);
            GLFW.glfwSetWindowFocusCallback(renderWindow, null);

            initialized = false;
        }
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

    private void handleBeginFrame(final BeginFrame evt) {

    }

    private void handleGLFWKeyCallback(long window, int key, int scancode, int action, int mods) {
        if (window == graphics.getRenderWindow()) {

        }
    }

    private void handleGLFWMouseButtonCallback(long window, int button, int action, int mods) {
        if (window == graphics.getRenderWindow()) {
            if (action == GLFW.GLFW_PRESS) {
                mouseButtonsDown |= button;
                mouseButtonsPressed |= button;

                publish(MouseButtonDown.builder()
                        .button(button));
            } else {
                mouseButtonsDown &= ~button;

                publish(MouseButtonRelease.builder()
                        .button(button));
            }
        }
    }

    private void handleGLFWCursorPosCallback(long window, double xpos, double ypos) {
        if (window == graphics.getRenderWindow()) {
            Vector2d position = new Vector2d(xpos, ypos);
            Vector2d relative = new Vector2d(mouseLastPosition).sub(position);

            mouseLastPosition = position;
            mouseMoved.add(relative);

            publish(MouseMove.builder()
                    .position(position)
                    .relative(relative));
        }
    }

    private void handleGLFWScrollCallback(long window, double xoffset, double yoffset) {
        if (window == graphics.getRenderWindow()) {
            double amount = xoffset > yoffset ? xoffset : -yoffset;

            mouseWheelMoved += amount;

            publish(MouseScroll.builder()
                    .amount(amount));
        }
    }
}
