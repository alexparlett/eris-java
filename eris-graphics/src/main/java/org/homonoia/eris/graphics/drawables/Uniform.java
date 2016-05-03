package org.homonoia.eris.graphics.drawables;

/**
 * Created by alexparlett on 16/04/2016.
 */
public class Uniform {
    private int type;
    private int location;
    private Object data;

    private Uniform(Builder builder) {
        this.type = builder.type;
        this.location = builder.location;
        this.data = builder.data;
    }

    public int getType() {
        return type;
    }

    public int getLocation() {
        return location;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public static final class Builder {
        private int type;
        private int location;
        private Object data;

        private Builder() {
        }

        public Uniform build() {
            return new Uniform(this);
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder location(int location) {
            this.location = location;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }
    }
}