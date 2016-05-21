package org.homonoia.eris.math;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector4d extends org.joml.Vector4d {

    public Vector4d() {
    }

    public Vector4d(final Vector4d v) {
        super(v);
    }

    public Vector4d(final Vector3d v, final double w) {
        super(v, w);
    }

    public Vector4d(final Vector2d v, final double z, final double w) {
        super(v, z, w);
    }

    public Vector4d(final double d) {
        super(d);
    }

    public Vector4d(final Vector4f v) {
        super(v);
    }

    public Vector4d(final Vector3f v, final double w) {
        super(v, w);
    }

    public Vector4d(final Vector2f v, final double z, final double w) {
        super(v, z, w);
    }

    public Vector4d(final double x, final double y, final double z, final double w) {
        super(x, y, z, w);
    }

    public Vector4d(final ByteBuffer buffer) {
        super(buffer);
    }

    public Vector4d(final int index, final ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector4d(final DoubleBuffer buffer) {
        super(buffer);
    }

    public Vector4d(final int index, final DoubleBuffer buffer) {
        super(index, buffer);
    }

    public static Vector4d parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector4d(Double.parseDouble(tokens[0]),
                Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]),
                Double.parseDouble(tokens[3]));
    }
}
