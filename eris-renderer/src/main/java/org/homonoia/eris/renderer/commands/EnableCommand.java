package org.homonoia.eris.render.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.render.RenderCommand;
import org.homonoia.eris.render.RenderKey;
import org.homonoia.eris.render.Renderer;

import static org.lwjgl.opengl.GL11.glEnable;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class EnableCommand extends RenderCommand<EnableCommand> {

    private static final Pool<EnableCommand> POOL = new ExpandingPool<>(16, Integer.MAX_VALUE, () -> new EnableCommand());
    private int capability;

    @Override
    public void process(final Renderer renderer, final RenderKey renderKey) {
        glEnable(capability);
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public EnableCommand capability(int capability) {
        this.capability = capability;
        return this;
    }

    public static EnableCommand newInstance() {
        return POOL.obtain();
    }
}
