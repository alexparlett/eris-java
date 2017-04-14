package org.homonoia.eris.input;

import org.apache.commons.lang3.StringUtils;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.input.events.KeyDown;
import org.homonoia.eris.input.events.KeyUp;
import org.homonoia.eris.input.events.MouseButtonDown;
import org.homonoia.eris.input.events.MouseButtonUp;
import org.homonoia.eris.input.events.MouseMove;
import org.homonoia.eris.input.events.MouseScroll;
import org.homonoia.eris.input.events.Text;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwGetKeyName;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 01/03/2016
 */
public class Input extends Contextual {

    private final Graphics graphics;

    private boolean initialized = false;
    private long renderWindow = MemoryUtil.NULL;
    private Vector2d mouseLastPosition = new Vector2d();

    private GLFWKeyCallback glfwKeyCallback;
    private GLFWMouseButtonCallback glfwMouseButtonCallback;
    private GLFWCursorPosCallback glfwCursorPosCallback;
    private GLFWScrollCallback glfwScrollCallback;
    private GLFWCharCallback glfwCharCallback;

    public Input(final Context context, final Graphics graphics) {
        super(context);
        context.registerBean(this);
        this.graphics = graphics;
    }

    public void initialize() throws InitializationException {
        if (!initialized) {
            renderWindow = graphics.getRenderWindow();
            if (renderWindow == MemoryUtil.NULL) {
                throw new InitializationException("Cannot initialize input before graphics, window is not defined yet.");
            }

            GLFW.glfwSetInputMode(renderWindow, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
            GLFW.glfwSetInputMode(renderWindow, GLFW.GLFW_STICKY_KEYS, GLFW.GLFW_FALSE);
            GLFW.glfwSetInputMode(renderWindow, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GLFW.GLFW_FALSE);

            glfwKeyCallback = GLFWKeyCallback.create(this::handleGLFWKeyCallback);
            glfwMouseButtonCallback = GLFWMouseButtonCallback.create(this::handleGLFWMouseButtonCallback);
            glfwCursorPosCallback = GLFWCursorPosCallback.create(this::handleGLFWCursorPosCallback);
            glfwScrollCallback = GLFWScrollCallback.create(this::handleGLFWScrollCallback);
            glfwCharCallback = GLFWCharCallback.create(this::handleGLFWCharCallback);

            GLFW.glfwSetKeyCallback(renderWindow, glfwKeyCallback);
            GLFW.glfwSetMouseButtonCallback(renderWindow, glfwMouseButtonCallback);
            GLFW.glfwSetCursorPosCallback(renderWindow, glfwCursorPosCallback);
            GLFW.glfwSetScrollCallback(renderWindow, glfwScrollCallback);
            GLFW.glfwSetCharCallback(renderWindow, glfwCharCallback);

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
            GLFW.glfwSetCharCallback(renderWindow, null);

            glfwKeyCallback.free();
            glfwMouseButtonCallback.free();
            glfwCursorPosCallback.free();
            glfwScrollCallback.free();
            glfwCharCallback.free();

            unsubscribe();

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

    public void setMousePosition(int x, int y) {
        try (MemoryStack stack = stackPush()) {
            final IntBuffer xpos = stack.mallocInt(1);
            final IntBuffer ypos = stack.mallocInt(1);
            glfwGetWindowPos(renderWindow, xpos, ypos);
            glfwSetCursorPos(renderWindow, x - xpos.get(0), y - ypos.get(0));
        }
    }

    private void handleGLFWKeyCallback(long window, int key, int scancode, int action, int mods) {
        if (window == renderWindow) {
            String keyName = glfwGetKeyName(key, scancode);
            if (action == GLFW.GLFW_PRESS) {
                publish(KeyDown.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .character(!StringUtils.isEmpty(keyName) ? keyName.charAt(0) : Character.MIN_VALUE)
                        .mods(extractModifiers(mods))
                        .repeat(false));
            } else if (action == GLFW.GLFW_REPEAT) {
                publish(KeyDown.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .character(!StringUtils.isEmpty(keyName) ? keyName.charAt(0) : Character.MIN_VALUE)
                        .mods(extractModifiers(mods))
                        .repeat(true));
            } else {
                publish(KeyUp.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .character(!StringUtils.isEmpty(keyName) ? keyName.charAt(0) : Character.MIN_VALUE)
                        .mods(extractModifiers(mods)));
            }
        }
    }

    private void handleGLFWMouseButtonCallback(long window, int button, int action, int mods) {
        if (window == renderWindow) {
            if (action == GLFW.GLFW_PRESS) {
                publish(MouseButtonDown.builder()
                        .button(Button.valueOf(button))
                        .mods(extractModifiers(mods))
                        .position(mouseLastPosition));
            } else {
                publish(MouseButtonUp.builder()
                        .button(Button.valueOf(button))
                        .mods(extractModifiers(mods))
                        .position(mouseLastPosition));
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
                    .delta(delta)
                    .position(mouseLastPosition));
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