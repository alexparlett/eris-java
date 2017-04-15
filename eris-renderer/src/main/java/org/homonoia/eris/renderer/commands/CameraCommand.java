package org.homonoia.eris.render.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.render.RenderCommand;
import org.homonoia.eris.render.RenderKey;
import org.homonoia.eris.render.Renderer;
import org.joml.Matrix4f;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class CameraCommand extends RenderCommand<CameraCommand> {

    private static final Pool<CameraCommand> POOL = new ExpandingPool<>(4, Integer.MAX_VALUE, () -> new CameraCommand());
    private Matrix4f view = new Matrix4f().identity();
    private Matrix4f projection = new Matrix4f().identity();

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
        this.view.set(view);
        return this;
    }

    public CameraCommand projection(Matrix4f projection) {
        this.projection.set(projection);
        return this;
    }

    public static CameraCommand newInstance() {
        return POOL.obtain();
    }
}
