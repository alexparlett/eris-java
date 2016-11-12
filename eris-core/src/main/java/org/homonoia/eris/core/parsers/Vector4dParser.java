package org.homonoia.eris.core.parsers;

import org.joml.Vector4d;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector4dParser {
    public static Vector4d parse(final String asString) throws ParseException {
        String[] tokens = asString.trim().split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector4d(Double.parseDouble(tokens[0]),
                Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]),
                Double.parseDouble(tokens[3]));
    }
}
