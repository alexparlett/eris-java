package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.joml.Matrix4f;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class CameraCommand extends RenderCommand<CameraCommand> {

    private static final Pool<CameraCommand> POOL = new ExpandingPool<>(4, Integer.MAX_VALUE, () -> new CameraCommand());
    private Matrix4f view;
    private Matrix4f projection;

    @Override
    public void process(final Renderer renderer, final RenderKey renderKey) {
        renderer.setCurrentProjection(projection);
        renderer.setCurrentView(view);
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public CameraCommand view(Matrix4f view) {
        this.view = view;
        return this;
    }

    public CameraCommand projection(Matrix4f projection) {
        this.projection = projection;
        return this;
    }

    public static CameraCommand newInstance() {
        return POOL.obtain();
    }
}
