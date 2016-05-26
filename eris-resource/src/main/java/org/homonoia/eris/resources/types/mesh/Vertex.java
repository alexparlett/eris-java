package org.homonoia.eris.resources.types.mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class Vertex {
    // Vertex data
    private Vector3f position;
    private Vector3f normal;
    private Vector2f texCoords;

    // The amount of elements that a vertex has
    public static final int COUNT = 8;
    // The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
    public static final int SIZE_OF = Float.BYTES * COUNT;

    private Vertex(Builder builder) {
        this.position = builder.position;
        this.normal = builder.normal;
        this.texCoords = builder.texCoords;
    }

    // Getters
    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Vector2f getTexCoords() {
        return texCoords;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (position != null ? !position.equals(vertex.position) : vertex.position != null) return false;
        if (normal != null ? !normal.equals(vertex.normal) : vertex.normal != null) return false;
        return texCoords != null ? texCoords.equals(vertex.texCoords) : vertex.texCoords == null;

    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + (normal != null ? normal.hashCode() : 0);
        result = 31 * result + (texCoords != null ? texCoords.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return position.x + ", " + position.y + ", " + position.z + ", "
                + normal.x + ", " + normal.y + ", " + normal.z + ", "
                + texCoords.x + ", " + texCoords.y + ", ";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Vector3f position;
        private Vector3f normal;
        private Vector2f texCoords;

        private Builder() {
        }

        public Vertex build() {
            return new Vertex(this);
        }

        public Builder position(Vector3f position) {
            this.position = position;
            return this;
        }

        public Builder normal(Vector3f normal) {
            this.normal = normal;
            return this;
        }

        public Builder texCoords(Vector2f texCoords) {
            this.texCoords = texCoords;
            return this;
        }
    }
}
