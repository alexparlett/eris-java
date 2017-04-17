package org.homonoia.eris.core.parsers;

import org.joml.Vector3i;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector3iParser {
    public static Vector3i parse(final String asString) throws ParseException {
        String[] tokens = asString.trim().split(" ");
        if (tokens.length != 3) {
            throw new ParseException("Cannot parse Vector3i from " + asString + " invalid number of arguments", 0);
        }

        return new Vector3i(Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
    }
}
