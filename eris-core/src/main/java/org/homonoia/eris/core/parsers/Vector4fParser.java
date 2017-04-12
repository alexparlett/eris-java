package org.homonoia.eris.core.parsers;

import org.joml.Vector4f;

import java.text.ParseException;

/**
 * Created by alexparlett on 02/05/2016.
 */
public class Vector4fParser {
    public static Vector4f parse(final String asString) throws ParseException {
        String[] tokens = asString.trim().split(" ");
        if (tokens.length != 4) {
            throw new ParseException("Cannot parse Vector4f from " + asString + " invalid number of arguments", 0);
        }

        return new Vector4f(Float.parseFloat(tokens[0]),
                Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),
                Float.parseFloat(tokens[3]));
    }
}
