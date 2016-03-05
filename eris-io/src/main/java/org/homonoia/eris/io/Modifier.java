package org.homonoia.eris.io;

import org.lwjgl.glfw.GLFW;

/**
 * Created by alexp on 05/03/2016.
 */
public enum Modifier {

    SHIFT(GLFW.GLFW_MOD_SHIFT),
    CTRL(GLFW.GLFW_MOD_CONTROL),
    ALT(GLFW.GLFW_MOD_ALT),
    SUPER(GLFW.GLFW_MOD_SUPER);

    private int modifierCode;

    Modifier(final int modifierCode) {
        this.modifierCode = modifierCode;
    }

    public int getModifierCode() {
        return modifierCode;
    }

    public static Modifier valueOf(final int modifierCode) {
        switch (modifierCode) {
            case GLFW.GLFW_MOD_SHIFT:
                return SHIFT;
            case GLFW.GLFW_MOD_CONTROL:
                return CTRL;
            case GLFW.GLFW_MOD_ALT:
                return ALT;
            case GLFW.GLFW_MOD_SUPER:
                return SUPER;
        }
        return null;
    }
}
