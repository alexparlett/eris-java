package org.homonoia.eris.core.parsers;

import org.joml.Matrix4f;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Matrix4fParser {
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
