package org.homonoia.eris.io;

import org.lwjgl.glfw.GLFW;

/**
 * Created by alexp on 05/03/2016.
 */
public enum Key {

    UNKNOWN(GLFW.GLFW_KEY_UNKNOWN);

    private int keyCode;

    private Key(final int keyCode) {
        this.keyCode = keyCode;
    }
    
    public static Key valueOf(final int keyCode) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_UNKNOWN:
                break;
            case GLFW.GLFW_KEY_SPACE:
                break;
            case GLFW.GLFW_KEY_APOSTROPHE:
                break;
            case GLFW.GLFW_KEY_COMMA:
                break;
            case GLFW.GLFW_KEY_MINUS:
                break;
            case GLFW.GLFW_KEY_PERIOD:
                break;
            case GLFW.GLFW_KEY_SLASH:
                break;
            case GLFW.GLFW_KEY_0:
                break;
            case GLFW.GLFW_KEY_1:
                break;
            case GLFW.GLFW_KEY_2:
                break;
            case GLFW.GLFW_KEY_3:
                break;
            case GLFW.GLFW_KEY_4:
                break;
            case GLFW.GLFW_KEY_5:
                break;
            case GLFW.GLFW_KEY_6:
                break;
            case GLFW.GLFW_KEY_7:
                break;
            case GLFW.GLFW_KEY_8:
                break;
            case GLFW.GLFW_KEY_9:
                break;
            case GLFW.GLFW_KEY_SEMICOLON:
                break;
            case GLFW.GLFW_KEY_EQUAL:
                break;
            case GLFW.GLFW_KEY_A:
                break;
            case GLFW.GLFW_KEY_B:
                break;
            case GLFW.GLFW_KEY_C:
                break;
            case GLFW.GLFW_KEY_D:
                break;
            case GLFW.GLFW_KEY_E:
                break;
            case GLFW.GLFW_KEY_F:
                break;
            case GLFW.GLFW_KEY_G:
                break;
            case GLFW.GLFW_KEY_H:
                break;
            case GLFW.GLFW_KEY_I:
                break;
            case GLFW.GLFW_KEY_J:
                break;
            case GLFW.GLFW_KEY_K:
                break;
            case GLFW.GLFW_KEY_L:
                break;
            case GLFW.GLFW_KEY_M:
                break;
            case GLFW.GLFW_KEY_N:
                break;
            case GLFW.GLFW_KEY_O:
                break;
            case GLFW.GLFW_KEY_P:
                break;
            case GLFW.GLFW_KEY_Q:
                break;
            case GLFW.GLFW_KEY_R:
                break;
            case GLFW.GLFW_KEY_S:
                break;
            case GLFW.GLFW_KEY_T:
                break;
            case GLFW.GLFW_KEY_U:
                break;
            case GLFW.GLFW_KEY_V:
                break;
            case GLFW.GLFW_KEY_W:
                break;
            case GLFW.GLFW_KEY_X:
                break;
            case GLFW.GLFW_KEY_Y:
                break;
            case GLFW.GLFW_KEY_Z:
                break;
            case GLFW.GLFW_KEY_LEFT_BRACKET:
                break;
            case GLFW.GLFW_KEY_BACKSLASH:
                break;
            case GLFW.GLFW_KEY_RIGHT_BRACKET:
                break;
            case GLFW.GLFW_KEY_GRAVE_ACCENT:
                break;
            case GLFW.GLFW_KEY_WORLD_1:
                break;
            case GLFW.GLFW_KEY_WORLD_2:
                break;
            case GLFW.GLFW_KEY_ESCAPE:
                break;
            case GLFW.GLFW_KEY_ENTER:
                break;
            case GLFW.GLFW_KEY_TAB:
                break;
            case GLFW.GLFW_KEY_BACKSPACE:
                break;
            case GLFW.GLFW_KEY_INSERT:
                break;
            case GLFW.GLFW_KEY_DELETE:
                break;
            case GLFW.GLFW_KEY_RIGHT:
                break;
            case GLFW.GLFW_KEY_LEFT:
                break;
            case GLFW.GLFW_KEY_DOWN:
                break;
            case GLFW.GLFW_KEY_UP:
                break;
            case GLFW.GLFW_KEY_PAGE_UP:
                break;
            case GLFW.GLFW_KEY_PAGE_DOWN:
                break;
            case GLFW.GLFW_KEY_HOME:
                break;
            case GLFW.GLFW_KEY_END:
                break;
            case GLFW.GLFW_KEY_CAPS_LOCK:
                break;
            case GLFW.GLFW_KEY_SCROLL_LOCK:
                break;
            case GLFW.GLFW_KEY_NUM_LOCK:
                break;
            case GLFW.GLFW_KEY_PRINT_SCREEN:
                break;
            case GLFW.GLFW_KEY_PAUSE:
                break;
            case GLFW.GLFW_KEY_F1:
                break;
            case GLFW.GLFW_KEY_F2:
                break;
            case GLFW.GLFW_KEY_F3:
                break;
            case GLFW.GLFW_KEY_F4:
                break;
            case GLFW.GLFW_KEY_F5:
                break;
            case GLFW.GLFW_KEY_F6:
                break;
            case GLFW.GLFW_KEY_F7:
                break;
            case GLFW.GLFW_KEY_F8:
                break;
            case GLFW.GLFW_KEY_F9:
                break;
            case GLFW.GLFW_KEY_F10:
                break;
            case GLFW.GLFW_KEY_F11:
                break;
            case GLFW.GLFW_KEY_F12:
                break;
            case GLFW.GLFW_KEY_F13:
                break;
            case GLFW.GLFW_KEY_F14:
                break;
            case GLFW.GLFW_KEY_F15:
                break;
            case GLFW.GLFW_KEY_F16:
                break;
            case GLFW.GLFW_KEY_F17:
                break;
            case GLFW.GLFW_KEY_F18:
                break;
            case GLFW.GLFW_KEY_F19:
                break;
            case GLFW.GLFW_KEY_F20:
                break;
            case GLFW.GLFW_KEY_F21:
                break;
            case GLFW.GLFW_KEY_F22:
                break;
            case GLFW.GLFW_KEY_F23:
                break;
            case GLFW.GLFW_KEY_F24:
                break;
            case GLFW.GLFW_KEY_F25:
                break;
            case GLFW.GLFW_KEY_KP_0:
                break;
            case GLFW.GLFW_KEY_KP_1:
                break;
            case GLFW.GLFW_KEY_KP_2:
                break;
            case GLFW.GLFW_KEY_KP_3:
                break;
            case GLFW.GLFW_KEY_KP_4:
                break;
            case GLFW.GLFW_KEY_KP_5:
                break;
            case GLFW.GLFW_KEY_KP_6:
                break;
            case GLFW.GLFW_KEY_KP_7:
                break;
            case GLFW.GLFW_KEY_KP_8:
                break;
            case GLFW.GLFW_KEY_KP_9:
                break;
            case GLFW.GLFW_KEY_KP_DECIMAL:
                break;
            case GLFW.GLFW_KEY_KP_DIVIDE:
                break;
            case GLFW.GLFW_KEY_KP_MULTIPLY:
                break;
            case GLFW.GLFW_KEY_KP_SUBTRACT:
                break;
            case GLFW.GLFW_KEY_KP_ADD:
                break;
            case GLFW.GLFW_KEY_KP_ENTER:
                break;
            case GLFW.GLFW_KEY_KP_EQUAL:
                break;
            case GLFW.GLFW_KEY_LEFT_SHIFT:
                break;
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                break;
            case GLFW.GLFW_KEY_LEFT_ALT:
                break;
            case GLFW.GLFW_KEY_LEFT_SUPER:
                break;
            case GLFW.GLFW_KEY_RIGHT_SHIFT:
                break;
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
                break;
            case GLFW.GLFW_KEY_RIGHT_ALT:
                break;
            case GLFW.GLFW_KEY_RIGHT_SUPER:
                break;
            case GLFW.GLFW_KEY_MENU:
                break;
        }
        return UNKNOWN;
    }
}
