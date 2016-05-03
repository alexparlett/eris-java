package org.homonoia.eris.math;

import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector4i extends org.joml.Vector4i {

    public Vector4i() {
    }

    public Vector4i(final  Vector4i v) {
        super(v);
    }

    public Vector4i(final Vector3i v, final int w) {
        super(v, w);
    }

    public Vector4i(final Vector2i v, final int z, final int w) {
        super(v, z, w);
    }

    public Vector4i(final int s) {
        super(s);
    }

    public Vector4i(final int x, final int y, final int z, final int w) {
        super(x, y, z, w);
    }

    public Vector4i(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector4i(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector4i(final IntBuffer buffer) {
        super(buffer);
    }

    public Vector4i(final int index, final IntBuffer buffer) {
        super(index, buffer);
    }

    public static Vector4i parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector4i(Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
                Integer.parseInt(tokens[3]));
    }
}
