package org.homonoia.eris.math;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector2i extends org.joml.Vector2i {

    public Vector2i() {
    }

    public Vector2i(final int s) {
        super(s);
    }

    public Vector2i(final int x, final int y) {
        super(x, y);
    }

    public Vector2i(final Vector2i v) {
        super(v);
    }

    public Vector2i(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector2i(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector2i(final IntBuffer buffer) {
        super(buffer);
    }

    public Vector2i(final int index, final IntBuffer buffer) {
        super(index, buffer);
    }

    public static Vector2i parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector2i(Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]));
    }
}
