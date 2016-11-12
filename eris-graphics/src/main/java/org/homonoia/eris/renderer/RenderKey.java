package org.homonoia.eris.renderer;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class RenderKey {

    private long key = 0L;
    private long target; //The RenderTarget
    private long targetLayer; //The Layer in the Render Target
    private long transparency; //Whether there is transparency
    private long command; //The command id
    private long extra;
    private long depth; //the depth of the object
    private long material; //The material

    public RenderKey(final Builder builder) {

        target = builder.target;
        targetLayer = builder.targetLayer;
        transparency = builder.transparency;
        command = builder.command;
        extra = builder.extra;
        depth = builder.depth;
        material = builder.material;

        if (transparency != 0L) {
            key = target << 62;
            key |= targetLayer << 60;
            key |= command << 58;
            key |= transparency << 57;
            key |= extra << 56;
            key |= -depth << 32;
            key |= material;
        } else {
            key = target << 62;
            key |= targetLayer << 60;
            key |= command << 58;
            key |= transparency << 57;
            key |= extra << 56;
            key |= material << 24;
            key |= depth;
        }
    }

    public Long getKey() {
        return key;
    }

    public long getTarget() {
        return target;
    }

    public long getTargetLayer() {
        return targetLayer;
    }

    public long getTransparency() {
        return transparency;
    }

    public long getCommand() {
        return command;
    }

    public long getExtra() {
        return extra;
    }

    public long getDepth() {
        return depth;
    }

    public long getMaterial() {
        return material;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private long target;
        private long targetLayer;
        private long transparency;
        private long command;
        private long extra;
        private long depth;
        private long material;

        public Builder target(long target) {
            this.target = target;
            return this;
        }

        public Builder targetLayer(long targetLayer) {
            this.targetLayer = targetLayer;
            return this;
        }

        public Builder transparency(long transparency) {
            this.transparency = transparency;
            return this;
        }

        public Builder command(long command) {
            this.command = command;
            return this;
        }

        public Builder extra(long extra) {
            this.extra = extra;
            return this;
        }

        public Builder depth(long depth) {
            this.depth = depth;
            return this;
        }

        public Builder material(long material) {
            this.material = material;
            return this;
        }

        public RenderKey build() {
            return new RenderKey(this);
        }
    }

}
