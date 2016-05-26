package org.homonoia.eris.core.parsers;

import org.joml.Matrix4d;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Matrix4dParser {
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
