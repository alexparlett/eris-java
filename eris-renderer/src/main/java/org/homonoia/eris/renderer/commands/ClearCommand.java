package org.homonoia.eris.render.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.render.RenderCommand;
import org.homonoia.eris.render.RenderKey;
import org.homonoia.eris.render.Renderer;

import static org.lwjgl.opengl.GL11.glClear;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class ClearCommand extends RenderCommand<ClearCommand> {

    private static final Pool<ClearCommand> POOL = new ExpandingPool<>(4, Integer.MAX_VALUE, () -> new ClearCommand());
    private int bitfield;

    @Override
    public void process(final Renderer renderer, final RenderKey renderKey) {
        glClear(bitfield);
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public ClearCommand bitfield(int bitfield) {
        this.bitfield = bitfield;
        return this;
    }

    public static ClearCommand newInstance() {
        return POOL.obtain();
    }
}
