package org.homonoia.eris.core.parsers;

import org.joml.Vector4i;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector4iParser {
    public static Vector4i parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector4i(Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
                Integer.parseInt(tokens[3]));
    }
}
