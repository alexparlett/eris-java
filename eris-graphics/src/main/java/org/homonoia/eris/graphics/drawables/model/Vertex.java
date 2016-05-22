package org.homonoia.eris.graphics.drawables.model;

import java.util.Arrays;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class Vertex {
    // Vertex data
    private float[] position = new float[] {0f, 0f, 0f};
    private float[] normal = new float[] {1f, 1f, 1f};
    private float[] texCoords = new float[] {0f, 0f};

    // The amount of elements that a vertex has
    public static final int COUNT = 9;
    // The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
    public static final int SIZE_OF = Float.BYTES * COUNT;

    private Vertex(Builder builder) {
        this.position = builder.position;
        this.normal = builder.normal;
        this.texCoords = builder.texCoords;
    }

    // Getters
    public float[] getPosition() {
        return Arrays.copyOf(position, 3);
    }

    public float[] getNormal() {
        return Arrays.copyOf(normal, 3);
    }

    public float[] getTexCoords() {
        return Arrays.copyOf(texCoords, 2);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private float[] position;
        private float[] normal;
        private float[] texCoords;

        private Builder() {
        }

        public Vertex build() {
            return new Vertex(this);
        }

        public Builder position(float[] position) {
            this.position = position;
            return this;
        }

        public Builder normal(float[] normal) {
            this.normal = normal;
            return this;
        }

        public Builder texCoords(float[] texCoords) {
            this.texCoords = texCoords;
            return this;
        }
    }
}
