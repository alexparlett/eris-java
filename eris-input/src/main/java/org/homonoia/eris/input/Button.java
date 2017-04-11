package org.homonoia.eris.input;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 05/03/2016
 */
public enum Button {

    LEFT(GLFW_MOUSE_BUTTON_LEFT),
    RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
    MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
    BUTTON_4(GLFW_MOUSE_BUTTON_4),
    BUTTON_5(GLFW_MOUSE_BUTTON_5),
    BUTTON_6(GLFW_MOUSE_BUTTON_6),
    BUTTON_7(GLFW_MOUSE_BUTTON_7),
    BUTTON_8(GLFW_MOUSE_BUTTON_8),
    UNKNOWN(-1);

    private final int buttonCode;

    Button(final int buttonCode) {
        this.buttonCode = buttonCode;
    }

    public int getButtonCode() {
        return buttonCode;
    }

    public static Button valueOf(final int buttonCode) {
        switch (buttonCode) {
            case GLFW_MOUSE_BUTTON_LEFT:
                return LEFT;
            case GLFW_MOUSE_BUTTON_RIGHT:
                return RIGHT;
            case GLFW_MOUSE_BUTTON_MIDDLE:
                return MIDDLE;
            case GLFW_MOUSE_BUTTON_4:
                return BUTTON_4;
            case GLFW_MOUSE_BUTTON_5:
                return BUTTON_5;
            case GLFW_MOUSE_BUTTON_6:
                return BUTTON_6;
            case GLFW_MOUSE_BUTTON_7:
                return BUTTON_7;
            case GLFW_MOUSE_BUTTON_8:
                return BUTTON_8;
        }
        return UNKNOWN;
    }
}
