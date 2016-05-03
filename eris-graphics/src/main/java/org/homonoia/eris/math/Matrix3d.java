package org.homonoia.eris.math;

import org.joml.Matrix4d;
import org.joml.Matrix4f;

import java.nio.DoubleBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Matrix3d extends org.joml.Matrix3d {

    public Matrix3d() {
    }

    public Matrix3d(final Matrix3d mat) {
        super(mat);
    }

    public Matrix3d(final Matrix3f mat) {
        super(mat);
    }

    public Matrix3d(final Matrix4f mat) {
        super(mat);
    }

    public Matrix3d(final Matrix4d mat) {
        super(mat);
    }

    public Matrix3d(final double m00, final double m01, final double m02, final double m10, final double m11, final double m12, final double m20, final double m21, final double m22) {
        super(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    public Matrix3d(final DoubleBuffer buffer) {
        super(buffer);
    }

    public static Matrix3d parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" \n\r");
        if (tokens.length != 9) {
            throw new ParseException("Cannot parse Matrix3d from " + asString + " invalid number of arguments", 0);
        }

        return new Matrix3d(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]),
                Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]),
                Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]),
                Double.parseDouble(tokens[6]), Double.parseDouble(tokens[7]),
                Double.parseDouble(tokens[8]));
    }
}
