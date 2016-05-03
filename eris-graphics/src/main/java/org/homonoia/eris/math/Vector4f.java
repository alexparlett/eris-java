package org.homonoia.eris.math;

import org.joml.Vector2f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector4f extends org.joml.Vector4f {

    public Vector4f() {
    }

    public Vector4f(final Vector4f v) {
        super(v);
    }

    public Vector4f(final Vector3f v, final float w) {
        super(v, w);
    }

    public Vector4f(final Vector2f v, final float z, final float w) {
        super(v, z, w);
    }

    public Vector4f(final float d) {
        super(d);
    }

    public Vector4f(final float x, final float y, final float z, final float w) {
        super(x, y, z, w);
    }

    public Vector4f(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector4f(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector4f(final FloatBuffer buffer) {
        super(buffer);
    }

    public Vector4f(final int index, final FloatBuffer buffer) {
        super(index, buffer);
    }

    public static Vector4f parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector4f(Float.parseFloat(tokens[0]),
                Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),
                Float.parseFloat(tokens[3]));
    }
}
