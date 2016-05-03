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
public class Vector3b implements Externalizable {

    public boolean x;
    public boolean y;
    public boolean z;

    public Vector3b() {
    }

    public Vector3b(boolean val) {
        this(val, val, val);
    }

    public Vector3b(final boolean x, final boolean y, final boolean z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3b(final Vector3b other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3b(final ByteBuffer buffer) {
        this(buffer.position(), buffer);
    }

    public Vector3b(final int position, final ByteBuffer buffer) {
        this.x = buffer.getInt(position) > 0;
        this.y = buffer.getInt(position + 4) > 0;
        this.z = buffer.getInt(position + 8) > 0;
    }

    public Vector3b set(boolean val) {
        return set(val, val, val);
    }

    public Vector3b set(final boolean x, final boolean y, final boolean z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3b set(final Vector3b other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3b set(final ByteBuffer buffer) {
        return set(buffer.position(), buffer);
    }

    public Vector3b set(final int position, final ByteBuffer buffer) {
        this.x = buffer.getInt(position) > 0;
        this.y = buffer.getInt(position + 4) > 0;
        this.z = buffer.getInt(position + 8) > 0;
        return this;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeBoolean(x);
        out.writeBoolean(y);
        out.writeBoolean(z);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        x = in.readBoolean();
        y = in.readBoolean();
        z = in.readBoolean();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3b vector3b = (Vector3b) o;

        if (x != vector3b.x) return false;
        if (y != vector3b.y) return false;
        return z == vector3b.z;

    }

    @Override
    public int hashCode() {
        int result = (x ? 1 : 0);
        result = 31 * result + (y ? 1 : 0);
        result = 31 * result + (z ? 1 : 0);
        return result;
    }

    public static Vector3b parse(final String asString) throws ParseException {
        String[] tokens = asString.split(" ");
        if (tokens.length != 3) {
            throw new ParseException("Cannot parse Vector3b from " + asString + " invalid number of arguments", 0);
        }

        return new Vector3b(Boolean.parseBoolean(tokens[0]),
                Boolean.parseBoolean(tokens[1]),
                Boolean.parseBoolean(tokens[2]));
    }
}
