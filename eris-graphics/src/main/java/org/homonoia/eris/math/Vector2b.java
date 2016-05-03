package org.homonoia.eris.math;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.text.ParseException;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class Vector2b implements Externalizable {

    public boolean x;
    public boolean y;

    public Vector2b() {
    }

    public Vector2b(boolean val) {
        this(val, val);
    }

    public Vector2b(final boolean x, final boolean y) {
        this.x = x;
        this.y = y;
    }

    public Vector2b(final Vector2b other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector2b(final ByteBuffer buffer) {
        this(buffer.position(), buffer);
    }

    public Vector2b(final int position, final ByteBuffer buffer) {
        this.x = buffer.getInt(position) > 0;
        this.y = buffer.getInt(position + 4) > 0;
    }

    public Vector2b set(boolean val) {
        return set(val, val);
    }

    public Vector2b set(final boolean x, final boolean y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2b set(final Vector2b other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vector2b set(final ByteBuffer buffer) {
        return set(buffer.position(), buffer);
    }

    public Vector2b set(final int position, final ByteBuffer buffer) {
        this.x = buffer.getInt(position) > 0;
        this.y = buffer.getInt(position + 4) > 0;
        return this;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeBoolean(x);
        out.writeBoolean(y);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        x = in.readBoolean();
        y = in.readBoolean();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2b vector2b = (Vector2b) o;

        if (x != vector2b.x) return false;
        return y == vector2b.y;

    }

    @Override
    public int hashCode() {
        int result = (x ? 1 : 0);
        result = 31 * result + (y ? 1 : 0);
        return result;
    }

    public static Vector2b parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 2) {
            throw new ParseException("Cannot parse Vector2b from " + asString + " invalid number of arguments", 0);
        }

        return new Vector2b(Boolean.parseBoolean(tokens[0]),
                Boolean.parseBoolean(tokens[1]));
    }
}
