package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.graphics.drawables.Skybox;
import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.ui.UI;
import org.joml.Matrix4f;

import static java.util.Objects.isNull;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class DrawSkyboxCommand extends RenderCommand<DrawSkyboxCommand> {

    private static final Pool<DrawSkyboxCommand> POOL = new ExpandingPool<>(4, Integer.MAX_VALUE, () -> new DrawSkyboxCommand());
    private Skybox skybox;

    @Override
    public void process(final Renderer renderer, UI ui, final RenderKey previousRenderKey) {
        if (isNull(previousRenderKey) || previousRenderKey.getMaterial() != getRenderKey().getMaterial()) {
            skybox.getMaterial().use();

            Matrix4f currentView = new Matrix4f(renderer.getCurrentView()).setTranslation(0,0,0);
            findAndBindUniform("view", skybox.getMaterial().getShaderProgram(), currentView);
            findAndBindUniform("projection", skybox.getMaterial().getShaderProgram(), renderer.getCurrentProjection());
        }

        skybox.use();
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public DrawSkyboxCommand skybox(Skybox skybox) {
        this.skybox = skybox;
        return this;
    }

    public static DrawSkyboxCommand newInstance() {
        return POOL.obtain();
    }
}
