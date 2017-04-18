package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.ecs.components.Transform;
import org.homonoia.eris.graphics.drawables.Material;
import org.homonoia.eris.graphics.drawables.model.SubModel;
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
public class DrawModelCommand extends RenderCommand<DrawModelCommand> {

    private static final Pool<DrawModelCommand> POOL = new ExpandingPool<>(4, Integer.MAX_VALUE, () -> new DrawModelCommand());
    private SubModel model;
    private Matrix4f transform = new Matrix4f().identity();
    private Material material;

    @Override
    public void process(final Renderer renderer, UI ui, final RenderKey previousRenderKey) {
        if (isNull(previousRenderKey) || previousRenderKey.getMaterial() != getRenderKey().getMaterial()) {
            material.use();

            findAndBindUniform("view", renderer, material.getShaderProgram(), renderer.getCurrentView());
            findAndBindUniform("projection", renderer, material.getShaderProgram(), renderer.getCurrentProjection());
        }

        findAndBindUniform("model", renderer, material.getShaderProgram(), transform);

        model.draw();
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public DrawModelCommand model(SubModel model) {
        this.model = model;
        return this;
    }

    public DrawModelCommand material(Material material) {
        this.material = material;
        return this;
    }

    public DrawModelCommand transform(Transform transform) {
        this.transform = transform.getModelMatrix(this.transform);
        return this;
    }

    public static DrawModelCommand newInstance() {
        return POOL.obtain();
    }
}
