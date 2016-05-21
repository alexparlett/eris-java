package org.homonoia.eris.math;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector3i extends org.joml.Vector3i {

    public Vector3i() {
    }

    public Vector3i(final int d) {
        super(d);
    }

    public Vector3i(final int x, final int y, final int z) {
        super(x, y, z);
    }

    public Vector3i(final Vector3i v) {
        super(v);
    }

    public Vector3i(final Vector2i v, final int z) {
        super(v, z);
    }

    public Vector3i(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector3i(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector3i(final IntBuffer buffer) {
        super(buffer);
    }

    public Vector3i(final int index, final IntBuffer buffer) {
        super(index, buffer);
    }

    public static Vector3i parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 3) {
            throw new ParseException("Cannot parse Vector3i from " + asString + " invalid number of arguments", 0);
        }

        return new Vector3i(Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
    }
}
