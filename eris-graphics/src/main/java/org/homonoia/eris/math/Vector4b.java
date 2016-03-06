package org.homonoia.eris.math;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class Vector4b implements Externalizable {

    public boolean x;
    public boolean y;
    public boolean z;
    public boolean w;

    public Vector4b() {
    }

    public Vector4b(boolean val) {
        this(val, val, val, val);
    }

    public Vector4b(final boolean x, final boolean y, final boolean z, final boolean w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4b(final Vector4b other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public Vector4b(final ByteBuffer buffer) {
        this(buffer.position(), buffer);
    }

    public Vector4b(final int position, final ByteBuffer buffer) {
        this.x = buffer.getInt(position) > 0;
        this.y = buffer.getInt(position + 4) > 0;
        this.z = buffer.getInt(position + 8) > 0;
        this.w = buffer.getInt(position + 12) > 0;
    }

    public Vector4b set(boolean val) {
        return set(val, val, val, val);
    }

    public Vector4b set(final boolean x, final boolean y, final boolean z, final boolean w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4b set(final Vector4b other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
        return this;
    }

    public Vector4b set(final ByteBuffer buffer) {
        return set(buffer.position(), buffer);
    }

    public Vector4b set(final int position, final ByteBuffer buffer) {
        this.x = buffer.getInt(position) > 0;
        this.y = buffer.getInt(position + 4) > 0;
        this.z = buffer.getInt(position + 8) > 0;
        this.w = buffer.getInt(position + 12) > 0;
        return this;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeBoolean(x);
        out.writeBoolean(y);
        out.writeBoolean(z);
        out.writeBoolean(w);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        x = in.readBoolean();
        y = in.readBoolean();
        z = in.readBoolean();
        w = in.readBoolean();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector4b vector4b = (Vector4b) o;

        if (x != vector4b.x) return false;
        if (y != vector4b.y) return false;
        if (z != vector4b.z) return false;
        return w == vector4b.w;

    }

    @Override
    public int hashCode() {
        int result = (x ? 1 : 0);
        result = 31 * result + (y ? 1 : 0);
        result = 31 * result + (z ? 1 : 0);
        result = 31 * result + (w ? 1 : 0);
        return result;
    }
}
