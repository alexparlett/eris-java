package org.homonoia.eris.core.parsers;

import org.joml.Vector3f;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector3fParser {
    public static Vector3f parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 3) {
            throw new ParseException("Cannot parse Vector3f from " + asString + " invalid number of arguments, found " + tokens.length, 0);
        }

        return new Vector3f(Float.parseFloat(tokens[0]),
                Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
    }
}
