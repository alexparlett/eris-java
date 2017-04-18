package org.homonoia.eris.graphics.drawables.material;

/**
 * Created by alexparlett on 03/05/2016.
 */
public enum Transparency {
    Transparent(1),
    Opaque(0);

    private int value;

    Transparency(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Transparency parse(final String string) {
        switch (string.toLowerCase()) {
            case "transparent":
                return Transparency.Transparent;
            case "opaque":
                return Transparency.Opaque;
        }
        throw new IllegalArgumentException("No constant found for " + string + " acceptable values (transparent|opaque)");
    }
}
