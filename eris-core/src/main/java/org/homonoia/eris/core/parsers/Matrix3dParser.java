package org.homonoia.eris.core.parsers;

import org.homonoia.eris.core.exceptions.ParseException;
import org.joml.Matrix3d;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Matrix3dParser {
    public Matrix3d parse(final String asString) throws ParseException {
        String[] tokens = asString.trim().split(" \n\r");
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
