package org.homonoia.eris.renderer;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class RenderKey {

    private long key = 0L;
    private long target;
    private long targetLayer;
    private long transparency;
    private long command;
    private long extra;
    private long depth;
    private long material;

    public RenderKey(final Builder builder) {

        target = builder.target;
        targetLayer = builder.targetLayer;
        transparency = builder.transparency;
        command = builder.command;
        extra = builder.extra;
        depth = builder.depth;
        material = builder.material;

        if (transparency != 0L) {
            key = target << 62
                    | targetLayer << 60
                    | transparency << 58
                    | command << 57
                    | extra << 54
                    | material << 24
                    | depth;
        } else {
            key = target << 62
                    | targetLayer << 60
                    | transparency << 58
                    | command << 57
                    | extra << 54
                    | depth << 32
                    | material;
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
