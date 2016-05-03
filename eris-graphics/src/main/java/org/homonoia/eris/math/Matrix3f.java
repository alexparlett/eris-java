package org.homonoia.eris.math;

import java.nio.FloatBuffer;
import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Matrix3f extends org.joml.Matrix3f {

    public Matrix3f() {
    }

    public Matrix3f(final Matrix3f mat) {
        super(mat);
    }

    public Matrix3f(final Matrix4f mat) {
        super(mat);
    }

    public Matrix3f(final float m00, final float m01, final float m02, final float m10, final float m11, final float m12, final float m20, final float m21, final float m22) {
        super(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    public Matrix3f(final FloatBuffer buffer) {
        super(buffer);
    }

    public static Matrix3f parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" \n\r");
        if (tokens.length != 9) {
            throw new ParseException("Cannot parse Matrix3f from " + asString + " invalid number of arguments", 0);
        }

        return new Matrix3f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]),
                Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]),
                Float.parseFloat(tokens[4]), Float.parseFloat(tokens[5]),
                Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]),
                Float.parseFloat(tokens[8]));
    }
}
