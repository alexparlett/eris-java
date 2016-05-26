package org.homonoia.eris.core.parsers;

import org.joml.Vector2d;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector2dParser {
    public static Vector2d parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector2d(Double.parseDouble(tokens[0]),
                Double.parseDouble(tokens[1]));
    }
}
