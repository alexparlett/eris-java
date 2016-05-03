package org.homonoia.eris.math;

import org.joml.Matrix3d;

import java.nio.DoubleBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Matrix4d extends org.joml.Matrix4d {

    public Matrix4d() {
    }

    public Matrix4d(final Matrix4d mat) {
        super(mat);
    }

    public Matrix4d(final Matrix4f mat) {
        super(mat);
    }

    public Matrix4d(final Matrix3d mat) {
        super(mat);
    }

    public Matrix4d(final double m00, final double m01, final double m02, final double m03, final double m10, final double m11, final double m12, final double m13, final double m20, final double m21, final double m22, final double m23, final double m30, final double m31, final double m32, final double m33) {
        super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public Matrix4d(final DoubleBuffer buffer) {
        super(buffer);
    }

    public static Matrix4d parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" \n\r");
        if (tokens.length != 16) {
            throw new ParseException("Cannot parse Matrix4d from " + asString + " invalid number of arguments", 0);
        }

        return new Matrix4d(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]),
                Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]),
                Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]),
                Double.parseDouble(tokens[6]), Double.parseDouble(tokens[7]),
                Double.parseDouble(tokens[8]), Double.parseDouble(tokens[9]),
                Double.parseDouble(tokens[10]), Double.parseDouble(tokens[11]),
                Double.parseDouble(tokens[12]), Double.parseDouble(tokens[13]),
                Double.parseDouble(tokens[14]), Double.parseDouble(tokens[15]));
    }
}
