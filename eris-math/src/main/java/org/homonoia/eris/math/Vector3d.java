package org.homonoia.eris.math;

import org.joml.Vector2d;
import org.joml.Vector2f;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector3d extends org.joml.Vector3d {

    public Vector3d() {
    }

    public Vector3d(final double d) {
        super(d);
    }

    public Vector3d(final double x, final double y, final double z) {
        super(x, y, z);
    }

    public Vector3d(final Vector3f v) {
        super(v);
    }

    public Vector3d(final Vector2f v, final double z) {
        super(v, z);
    }

    public Vector3d(final Vector3d v) {
        super(v);
    }

    public Vector3d(final Vector2d v, final double z) {
        super(v, z);
    }

    public Vector3d(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector3d(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector3d(final DoubleBuffer buffer) {
        super(buffer);
    }

    public Vector3d(final int index, final DoubleBuffer buffer) {
        super(index, buffer);
    }

    public static Vector3d parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 3) {
            throw new ParseException("Cannot parse Vector3d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector3d(Double.parseDouble(tokens[0]),
                Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
    }
}
