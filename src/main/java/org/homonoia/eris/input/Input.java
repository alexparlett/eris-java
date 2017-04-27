package org.homonoia.eris.input;

import org.apache.commons.lang3.StringUtils;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.input.KeyDown;
import org.homonoia.eris.events.input.KeyUp;
import org.homonoia.eris.events.input.MouseButtonDown;
import org.homonoia.eris.events.input.MouseButtonUp;
import org.homonoia.eris.events.input.MouseMove;
import org.homonoia.eris.events.input.MouseScroll;
import org.homonoia.eris.events.input.Text;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.ui.UI;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkMouse;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKeyName;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.nuklear.Nuklear.nk_input_begin;
import static org.lwjgl.nuklear.Nuklear.nk_input_end;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 01/03/2016
 */
public class Input extends Contextual {

    private final Graphics graphics;
    private final UI ui;

    private boolean initialized = false;
    private long renderWindow = MemoryUtil.NULL;
    private Vector2d mouseLastPosition;
    private double timeStep = 0;

    private GLFWKeyCallback glfwKeyCallback;
    private GLFWMouseButtonCallback glfwMouseButtonCallback;
    private GLFWCursorPosCallback glfwCursorPosCallback;
    private GLFWScrollCallback glfwScrollCallback;
    private GLFWCharCallback glfwCharCallback;

    public Input(final Context context, final Graphics graphics, final UI ui) {
        super(context);
        context.registerBean(this);
        this.ui = ui;
        this.graphics = graphics;
    }

    public void initialize() throws InitializationException {
        if (!initialized) {
            renderWindow = graphics.getRenderWindow();
            if (renderWindow == MemoryUtil.NULL) {
                throw new InitializationException("Cannot initialize input before graphics, window is not defined yet.");
            }

            glfwSetInputMode(renderWindow, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            glfwSetInputMode(renderWindow, GLFW.GLFW_STICKY_KEYS, GLFW.GLFW_FALSE);
            glfwSetInputMode(renderWindow, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GLFW.GLFW_FALSE);

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

            try (MemoryStack stack = MemoryStack.stackPush()) {
                DoubleBuffer xpos = stack.callocDouble(1);
                DoubleBuffer ypos = stack.callocDouble(1);
                glfwGetCursorPos(renderWindow, xpos, ypos);
                mouseLastPosition = new Vector2d(xpos.get(), ypos.get());
            }

            initialized = true;
        }
    }

    public void update(double timeStep) {
        this.timeStep = timeStep;

        NkContext ctx = ui.getCtx();
        long win = graphics.getRenderWindow();

        nk_input_begin(ctx);

        glfwPollEvents();

        NkMouse mouse = ctx.input().mouse();
        if (mouse.grab()) {
            glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        } else if (mouse.grabbed()) {
            float prevX = mouse.prev().x();
            float prevY = mouse.prev().y();
            glfwSetCursorPos(win, prevX, prevY);
            mouse.pos().x(prevX);
            mouse.pos().y(prevY);
        } else if (mouse.ungrab()) {
            glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }

        nk_input_end(ctx);
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

    private void handleGLFWKeyCallback(long window, int key, int scancode, int action, int mods) {
        if (window == renderWindow) {
            String keyName = glfwGetKeyName(key, scancode);
            if (action == GLFW.GLFW_PRESS) {
                publish(KeyDown.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .character(!StringUtils.isEmpty(keyName) ? keyName.charAt(0) : Character.MIN_VALUE)
                        .mods(extractModifiers(mods))
                        .repeat(false)
                        .timeStep(timeStep));
            } else if (action == GLFW.GLFW_REPEAT) {
                publish(KeyDown.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .character(!StringUtils.isEmpty(keyName) ? keyName.charAt(0) : Character.MIN_VALUE)
                        .mods(extractModifiers(mods))
                        .repeat(true)
                        .timeStep(timeStep));
            } else {
                publish(KeyUp.builder()
                        .key(Key.valueOf(key))
                        .scancode(scancode)
                        .character(!StringUtils.isEmpty(keyName) ? keyName.charAt(0) : Character.MIN_VALUE)
                        .mods(extractModifiers(mods))
                        .timeStep(timeStep));
            }
        }
    }

    private void handleGLFWMouseButtonCallback(long window, int button, int action, int mods) {
        if (window == renderWindow) {
            if (action == GLFW.GLFW_PRESS) {
                publish(MouseButtonDown.builder()
                        .button(Button.valueOf(button))
                        .mods(extractModifiers(mods))
                        .position(mouseLastPosition)
                        .timeStep(timeStep));
            } else {
                publish(MouseButtonUp.builder()
                        .button(Button.valueOf(button))
                        .mods(extractModifiers(mods))
                        .position(mouseLastPosition)
                        .timeStep(timeStep));
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
                    .delta(delta)
                    .timeStep(timeStep));
        }
    }

    private void handleGLFWScrollCallback(long window, double xoffset, double yoffset) {
        if (window == graphics.getRenderWindow()) {
            publish(MouseScroll.builder()
                    .delta(new Vector2d(xoffset, yoffset))
                    .position(mouseLastPosition)
                    .timeStep(timeStep));
        }
    }

    private void handleGLFWCharCallback(long window, int codepoint) {
        if (window == renderWindow) {
            publish(Text.builder()
                    .string(String.valueOf(Character.toChars(codepoint)))
                    .timeStep(timeStep));
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