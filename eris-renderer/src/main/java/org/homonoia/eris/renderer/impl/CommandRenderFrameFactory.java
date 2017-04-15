package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.core.collections.pools.PoolFactory;
import org.homonoia.eris.renderer.Renderer;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/11/2016
 */
public final class CommandRenderFrameFactory implements PoolFactory<CommandRenderFrame> {

    private Renderer renderer;

    public CommandRenderFrameFactory(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public CommandRenderFrame create() {
        return new CommandRenderFrame(renderer);
    }
}
