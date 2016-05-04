package org.homonoia.eris.graphics.drawables;

import org.lwjgl.opengl.GL11;

/**
 * Created by alexparlett on 03/05/2016.
 */
public enum CullMode {
    Back(GL11.GL_BACK),
    Front(GL11.GL_FRONT),
    FrontBack(GL11.GL_FRONT_AND_BACK);

    private final int glCull;

    CullMode(final int glCull) {
        this.glCull = glCull;
    }

    public int getGlCull() {
        return glCull;
    }

    public static CullMode parse(final String string) {
        switch (string.toLowerCase()) {
            case "back":
                return CullMode.Back;
            case "front":
                return CullMode.Front;
            case "fontback":
                return CullMode.FrontBack;
        }
        throw new IllegalArgumentException("No constant found for " + string + " acceptable values (back,front,frontback)");
    }
}
