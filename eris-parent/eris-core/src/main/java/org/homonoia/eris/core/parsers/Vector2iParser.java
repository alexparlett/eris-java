package org.homonoia.eris.core.parsers;

import org.joml.Vector2i;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector2iParser {
    public static Vector2i parse(final String asString) throws ParseException {
        String[] tokens = asString.trim().split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector2i(Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]));
    }
}
