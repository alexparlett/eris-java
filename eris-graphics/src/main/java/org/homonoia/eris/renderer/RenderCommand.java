package org.homonoia.eris.renderer;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public abstract class RenderCommand<T extends RenderCommand> {

    private RenderKey renderKey;

    public RenderKey getRenderKey() {
        return renderKey;
    }

    public T renderKey(RenderKey renderKey) {
        this.renderKey = renderKey;
        return (T) this;
    }

    public abstract void process(final Renderer renderer, final RenderKey renderKey);

    public abstract void free();
}
