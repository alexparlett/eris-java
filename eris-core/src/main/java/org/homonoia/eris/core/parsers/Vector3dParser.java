package org.homonoia.eris.core.parsers;

import org.joml.Vector3d;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector3dParser {
    public static Vector3d parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 3) {
            throw new ParseException("Cannot parse Vector3d from " + asString + " invalid number of arguments", 0);
        }

        return new Vector3d(Double.parseDouble(tokens[0]),
                Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
    }
}
