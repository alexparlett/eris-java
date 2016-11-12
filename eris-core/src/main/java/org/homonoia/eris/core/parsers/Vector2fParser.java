package org.homonoia.eris.core.parsers;

import org.joml.Vector2f;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector2fParser {
    public static Vector2f parse(final String asString) throws ParseException {
        String[] tokens = asString.trim().split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2f from " + asString + " invalid number of arguments", 0);
        }

        return new Vector2f(Float.parseFloat(tokens[0]),
                Float.parseFloat(tokens[1]));
    }
}
