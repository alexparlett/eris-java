package org.homonoia.eris.math;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector3f extends org.joml.Vector3f {

    public static final Vector3f ZERO = new Vector3f(0f, 0f, 0f);

    public Vector3f() {
    }

    public Vector3f(final float d) {
        super(d);
    }

    public Vector3f(final float x, final float y, final float z) {
        super(x, y, z);
    }

    public Vector3f(final Vector3f v) {
        super(v);
    }

    public Vector3f(final Vector2f v, final float z) {
        super(v, z);
    }

    public Vector3f(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector3f(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector3f(final FloatBuffer buffer) {
        super(buffer);
    }

    public Vector3f(final int index, final FloatBuffer buffer) {
        super(index, buffer);
    }

    public static Vector3f parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 3) {
            throw new ParseException("Cannot parse Vector3f from " + asString + " invalid number of arguments, found " + tokens.length, 0);
        }

        return new Vector3f(Float.parseFloat(tokens[0]),
                Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
    }
}
