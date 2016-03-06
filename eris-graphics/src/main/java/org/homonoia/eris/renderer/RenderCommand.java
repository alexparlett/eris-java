package org.homonoia.eris.renderer;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public abstract class RenderCommand {

    private final RenderKey renderKey;

    protected RenderCommand(RenderCommandBuilder<?> builder) {
        this.renderKey = builder.renderKey;
    }

    public RenderKey getRenderKey() {
        return renderKey;
    }

    public abstract void process(final Renderer renderer, final RenderKey renderKey);

    public static abstract class RenderCommandBuilder<T extends RenderCommandBuilder> {

        private RenderKey renderKey;

        public T renderKey(final RenderKey renderKey) {
            this.renderKey = renderKey;
            return (T) this;
        }

        public abstract <K extends RenderCommand> K build();
    }
}
