package org.homonoia.eris.core.parsers;

import org.homonoia.eris.core.exceptions.ParseException;
import org.joml.Matrix3f;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Matrix3fParser {
    public static Matrix3f parse(final String asString) throws ParseException {
        String[] tokens = asString.trim().split(" \n\r");
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
