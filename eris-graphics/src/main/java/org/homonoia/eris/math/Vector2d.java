package org.homonoia.eris.math;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector2d extends org.joml.Vector2d {

    public Vector2d() {
    }

    public Vector2d(final double d) {
        super(d);
    }

    public Vector2d(final double x, final double y) {
        super(x, y);
    }

    public Vector2d(final Vector2d v) {
        super(v);
    }

    public Vector2d(final Vector2f v) {
        super(v);
    }

    public Vector2d(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector2d(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector2d(final DoubleBuffer buffer) {
        super(buffer);
    }

    public Vector2d(final int index, final DoubleBuffer buffer) {
        super(index, buffer);
    }

    public static Vector2d parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector2d(Double.parseDouble(tokens[0]),
                Double.parseDouble(tokens[1]));
    }
}
