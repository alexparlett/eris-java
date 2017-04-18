package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.ui.UI;

import static org.lwjgl.opengl.GL11.glDisable;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class DisableCommand extends RenderCommand<DisableCommand> {

    private static final Pool<DisableCommand> POOL = new ExpandingPool<>(16, Integer.MAX_VALUE, () -> new DisableCommand());
    private int capability;

    @Override
    public void process(final Renderer renderer, UI ui, final RenderKey renderKey) {
        glDisable(capability);
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public DisableCommand capability(int capability) {
        this.capability = capability;
        return this;
    }

    public static DisableCommand newInstance() {
        return POOL.obtain();
    }
}
