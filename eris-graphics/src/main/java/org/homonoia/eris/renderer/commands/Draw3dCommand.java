package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.graphics.drawables.Material;
import org.homonoia.eris.graphics.drawables.model.SubModel;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.joml.Matrix4f;

import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class Draw3dCommand extends RenderCommand<Draw3dCommand> {

    private static final Pool<Draw3dCommand> POOL = new ExpandingPool<>(4, Integer.MAX_VALUE, () -> new Draw3dCommand());
    private SubModel model;
    private Material material;
    private Matrix4f transform = new Matrix4f().identity();

    @Override
    public void process(final Renderer renderer, final RenderKey previousRenderKey) {
        if (isNull(previousRenderKey) || previousRenderKey.getMaterial() != getRenderKey().getMaterial()) {
            material.use();

            findAndBindUniform("view", renderer, renderer.getCurrentView());
            findAndBindUniform("projection", renderer, renderer.getCurrentProjection());
        }

        findAndBindUniform("model", renderer, transform);

        model.draw(renderer);
    }

    private void findAndBindUniform(String uniform, Renderer renderer, Object value) {
        Optional<Uniform> shaderUniformMaybe = material.getShaderProgram().getUniform(uniform);
        Uniform shaderUniform = shaderUniformMaybe.orElseThrow(() -> new IllegalArgumentException("No uniforms in shader found for " + uniform));
        renderer.bindUniform(shaderUniform.getLocation(), shaderUniform.getType(), value);
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public Draw3dCommand model(SubModel model) {
        this.model = model;
        return this;
    }

    public Draw3dCommand material(Material material) {
        this.material = material;
        return this;
    }

    public Draw3dCommand transform(Matrix4f transform) {
        this.transform.set(transform);
        return this;
    }

    public static Draw3dCommand newInstance() {
        return POOL.obtain();
    }
}
