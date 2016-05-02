package org.homonoia.eris.graphics.drawables;

/**
 * Created by alexparlett on 01/05/2016.
 */
public class TextureUnit {
    private String uniform;
    private Texture texture;

    private TextureUnit(Builder builder) {
        this.uniform = builder.uniform;
        this.texture = builder.texture;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUniform() {
        return uniform;
    }

    public Texture getTexture() {
        return texture;
    }

    public static final class Builder {
        private String uniform;
        private Texture texture;

        private Builder() {
        }

        public TextureUnit build() {
            return new TextureUnit(this);
        }

        public Builder uniform(String uniform) {
            this.uniform = uniform;
            return this;
        }

        public Builder texture(Texture texture) {
            this.texture = texture;
            return this;
        }
    }
}
