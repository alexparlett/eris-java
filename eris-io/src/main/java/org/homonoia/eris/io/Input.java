package org.homonoia.eris.io;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.io.events.*;
import org.joml.Vector2d;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 01/03/2016
 */
@ContextualComponent
public class Input extends Contextual {

    private final Graphics graphics;

    private boolean initialized = false;
    private long renderWindow = MemoryUtil.NULL;
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
            GLFW.glfwSetCharCallback(renderWindow, GLFWCharCallback.create(this::handleGLFWCharCallback));

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

    public boolean isKeyDown(Key key) {
        Objects.requireNonNull(key);
        return GLFW.glfwGetKey(renderWindow, key.getKeyCode()) == GLFW.GLFW_PRESS;
    }

    public boolean isMouseButtonDown(Button button) {
        Objects.requireNonNull(button);
        return GLFW.glfwGetMouseButton(renderWindow, button.getButtonCode()) == GLFW.GLFW_PRESS;
    }

    public boolean isModifierDown(Modifier modifier) {
        switch (modifier) {
            case SHIFT:
                return isKeyDown(Key.LEFT_SHIFT) || isKeyDown(Key.RIGHT_SHIFT);
            case CTRL:
                return isKeyDown(Key.LEFT_CONTROL) || isKeyDown(Key.RIGHT_CONTROL);
            case ALT:
                return isKeyDown(Key.LEFT_ALT) || isKeyDown(Key.RIGHT_ALT);
            case SUPER:
                return isKeyDown(Key.LEFT_SUPER) || isKeyDown(Key.RIGHT_SUPER);
        }
        return false;
    }

    private void handleGLFWKeyCallback(long window, int key, int scancode, int action, int mods) {
        if (window == renderWindow) {
            if (action == GLFW.GLFW_PRESS) {
                publish(KeyDown.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .mods(extractModifiers(mods))
                        .repeat(false));
            } else if (action == GLFW.GLFW_REPEAT) {
                publish(KeyDown.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .mods(extractModifiers(mods))
                        .repeat(true));
            } else {
                publish(KeyUp.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .mods(extractModifiers(mods)));
            }
        }
    }

    private void handleGLFWMouseButtonCallback(long window, int button, int action, int mods) {
        if (window == renderWindow) {
            if (action == GLFW.GLFW_PRESS) {
                publish(MouseButtonDown.builder()
                        .button(Button.valueOf(button))
                        .mods(extractModifiers(mods)));
            } else {
                publish(MouseButtonUp.builder()
                        .button(Button.valueOf(button))
                        .mods(extractModifiers(mods)));
            }
        }
    }

    private void handleGLFWCursorPosCallback(long window, double xpos, double ypos) {
        if (window == renderWindow) {
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

    private void handleGLFWCharCallback(long window, int codepoint) {
        if (window == renderWindow) {
            publish(Text.builder()
                    .string(String.valueOf(Character.toChars(codepoint))));
        }
    }

    private List<Modifier> extractModifiers(final int mods) {
        List<Modifier> modifiers = new ArrayList<>();

        if ((mods & Modifier.SHIFT.getModifierCode()) == Modifier.SHIFT.getModifierCode()) {
            modifiers.add(Modifier.SHIFT);
        }

        if ((mods & Modifier.CTRL.getModifierCode()) == Modifier.CTRL.getModifierCode()) {
            modifiers.add(Modifier.CTRL);
        }

        if ((mods & Modifier.ALT.getModifierCode()) == Modifier.ALT.getModifierCode()) {
            modifiers.add(Modifier.ALT);
        }

        if ((mods & Modifier.SUPER.getModifierCode()) == Modifier.SUPER.getModifierCode()) {
            modifiers.add(Modifier.SUPER);
        }

        return modifiers;
    }
}
