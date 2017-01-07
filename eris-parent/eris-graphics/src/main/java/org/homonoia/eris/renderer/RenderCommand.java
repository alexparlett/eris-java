package org.homonoia.eris.renderer;

import org.homonoia.eris.graphics.drawables.ShaderProgram;
import org.homonoia.eris.graphics.drawables.sp.Uniform;

import java.util.Optional;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public abstract class RenderCommand<T extends RenderCommand> implements Comparable<T> {

    private RenderKey renderKey;

    public RenderKey getRenderKey() {
        return renderKey;
    }

    public T renderKey(RenderKey renderKey) {
        this.renderKey = renderKey;
        return (T) this;
    }

    public abstract void process(final Renderer renderer, final RenderKey renderKey);

    public abstract void free();

    @Override
    public int compareTo(T o) {
        return this.getRenderKey().getKey().compareTo(o.getRenderKey().getKey());
    }

    protected void findAndBindUniform(String uniform, Renderer renderer, ShaderProgram shaderProgram, Object value) {
        Optional<Uniform> shaderUniformMaybe = shaderProgram.getUniform(uniform);
        Uniform shaderUniform = shaderUniformMaybe.orElseThrow(() -> new IllegalArgumentException("No uniforms in shader found for " + uniform));
        renderer.bindUniform(shaderUniform.getLocation(), shaderUniform.getType(), value);
    }
}
