package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.graphics.drawables.model.AxisAlignedBoundingBox;
import org.homonoia.eris.graphics.drawables.model.SubModel;
import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.joml.Matrix4f;

import static java.util.Objects.isNull;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class DrawBoundingSphereCommand extends RenderCommand<DrawBoundingSphereCommand> {

    private static final Pool<DrawBoundingSphereCommand> POOL = new ExpandingPool<>(4, Integer.MAX_VALUE, () -> new DrawBoundingSphereCommand());
    private AxisAlignedBoundingBox aabb;
    private Matrix4f transform = new Matrix4f().identity();
    private SubModel model;

    @Override
    public void process(final Renderer renderer, final RenderKey previousRenderKey) {
        if (isNull(previousRenderKey) || previousRenderKey.getMaterial() != getRenderKey().getMaterial()) {
            model.getMaterial().use();

            findAndBindUniform("view", renderer, model.getMaterial().getShaderProgram(), renderer.getCurrentView());
            findAndBindUniform("projection", renderer, model.getMaterial().getShaderProgram(), renderer.getCurrentProjection());
        }

        float distance = aabb.getMax().distance(aabb.getMin()) / 2;
        findAndBindUniform("model", renderer, model.getMaterial().getShaderProgram(), transform.scale(Math.abs(distance)));

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        model.draw();
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public DrawBoundingSphereCommand boundingBox(AxisAlignedBoundingBox aabb) {
        this.aabb = aabb;
        return this;
    }

    public DrawBoundingSphereCommand transform(Matrix4f transform) {
        this.transform.set(transform);
        return this;
    }

    public DrawBoundingSphereCommand model(SubModel model) {
        this.model = model;
        return this;
    }

    public static DrawBoundingSphereCommand newInstance() {
        return POOL.obtain();
    }
}
