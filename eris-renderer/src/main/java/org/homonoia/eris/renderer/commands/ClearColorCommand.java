package org.homonoia.eris.render.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.render.RenderCommand;
import org.homonoia.eris.render.RenderKey;
import org.homonoia.eris.render.Renderer;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class ClearColorCommand extends RenderCommand<ClearColorCommand> {

    private static final Pool<ClearColorCommand> POOL = new ExpandingPool<>(4, Integer.MAX_VALUE, () -> new ClearColorCommand());
    private Vector4f color;

    @Override
    public void process(final Renderer renderer, final RenderKey renderKey) {
        glClearColor(color.x, color.y, color.z, color.w);
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public ClearColorCommand color(Vector4f color) {
        this.color = color;
        return this;
    }

    public static ClearColorCommand newInstance() {
        return POOL.obtain();
    }
}
