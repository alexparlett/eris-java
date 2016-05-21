package org.homonoia.eris.math;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector2f extends org.joml.Vector2f {

    public Vector2f() {
    }

    public Vector2f(final float d) {
        super(d);
    }

    public Vector2f(final float x, final float y) {
        super(x, y);
    }

    public Vector2f(final Vector2f v) {
        super(v);
    }

    public Vector2f(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector2f(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector2f(final FloatBuffer buffer) {
        super(buffer);
    }

    public Vector2f(final int index, final FloatBuffer buffer) {
        super(index, buffer);
    }

    public static Vector2f parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2f from " + asString + " invalid number of arguments", 0);
        }

        return new Vector2f(Float.parseFloat(tokens[0]),
                Float.parseFloat(tokens[1]));
    }
}
