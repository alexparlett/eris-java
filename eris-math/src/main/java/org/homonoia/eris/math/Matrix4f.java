package org.homonoia.eris.math;

import org.joml.Matrix4d;

import java.nio.FloatBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Matrix4f extends org.joml.Matrix4f {

    public Matrix4f() {
        super();
    }

    public Matrix4f(final Matrix3f mat) {
        super(mat);
    }

    public Matrix4f(final Matrix4f mat) {
        super(mat);
    }

    public Matrix4f(final Matrix4d mat) {
        super(mat);
    }

    public Matrix4f(final float m00, final float m01, final float m02, final float m03, final float m10, final float m11, final float m12, final float m13, final float m20, final float m21, final float m22, final float m23, final float m30, final float m31, final float m32, final float m33) {
        super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public Matrix4f(final FloatBuffer buffer) {
        super(buffer);
    }

    public static Matrix4f parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" \n\r");
        if (tokens.length != 16) {
            throw new ParseException("Cannot parse Matrix4f from " + asString + " invalid number of arguments", 0);
        }

        return new Matrix4f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]),
                Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]),
                Float.parseFloat(tokens[4]), Float.parseFloat(tokens[5]),
                Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]),
                Float.parseFloat(tokens[8]), Float.parseFloat(tokens[9]),
                Float.parseFloat(tokens[10]), Float.parseFloat(tokens[11]),
                Float.parseFloat(tokens[12]), Float.parseFloat(tokens[13]),
                Float.parseFloat(tokens[14]), Float.parseFloat(tokens[15]));
    }
}
