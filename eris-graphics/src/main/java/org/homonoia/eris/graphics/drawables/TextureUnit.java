package org.homonoia.eris.graphics.drawables;

/**
 * Created by alexparlett on 01/05/2016.
 */
public class TextureUnit {
    private String uniform;
    private Texture texture;
    private int unit;

    private TextureUnit(Builder builder) {
        this.uniform = builder.uniform;
        this.texture = builder.texture;
        this.unit = builder.unit;
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

    public int getUnit() {
        return unit;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextureUnit that = (TextureUnit) o;

        if (unit != that.unit) return false;
        if (uniform != null ? !uniform.equals(that.uniform) : that.uniform != null) return false;
        return texture != null ? texture.equals(that.texture) : that.texture == null;

    }

    @Override
    public int hashCode() {
        int result = uniform != null ? uniform.hashCode() : 0;
        result = 31 * result + (texture != null ? texture.hashCode() : 0);
        result = 31 * result + unit;
        return result;
    }

    public static final class Builder {
        private String uniform;
        private Texture texture;
        private int unit;

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

        public Builder unit(final int unit) {
            this.unit = unit;
            return this;
        }
    }
}
