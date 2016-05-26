package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.graphics.drawables.Material;
import org.homonoia.eris.graphics.drawables.ShaderProgram;
import org.homonoia.eris.graphics.drawables.model.SubModel;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;

import java.util.HashMap;
import java.util.Objects;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class Draw3dCommand extends RenderCommand {

    private SubModel model;

    protected Draw3dCommand(final Builder builder) {
        super(builder);
        this.model = builder.model;
    }

    @Override
    public void process(final Renderer renderer, final RenderKey renderKey) {
        Material material = model.getMaterial();
        ShaderProgram shaderProgram = material.getShaderProgram();

        if (Objects.isNull(renderKey) || renderKey.getMaterial() != getRenderKey().getMaterial()) {

            material.getUniform("view").orElseGet(() -> {
                Uniform shaderUniform = shaderProgram.getUniform("view").get();
                return Uniform.builder()
                        .location(shaderUniform.getLocation())
                        .type(shaderUniform.getType())
                        .data(renderer.getCurrentView())
                        .build();
            }).setData(renderer.getCurrentView());

            material.getUniform("perspective").orElseGet(() -> {
                Uniform shaderUniform = shaderProgram.getUniform("projection").get();
                return Uniform.builder()
                        .location(shaderUniform.getLocation())
                        .type(shaderUniform.getType())
                        .data(renderer.getCurrentPerspective())
                        .build();
            }).setData(renderer.getCurrentPerspective());

            material.use();
        }

        HashMap<String, Uniform> uniforms = new HashMap<>();
        uniforms.putAll(shaderProgram.getUniforms());
        uniforms.putAll(material.getUniforms());
        uniforms.putAll(model.getUniforms());

        uniforms.values().stream()
                .filter(uniform -> Objects.nonNull(uniform.getData()))
                .forEach(uniform -> renderer.bindUniform(uniform.getLocation(), uniform.getType(), uniform.getData()));

        model.draw();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends RenderCommandBuilder<Builder> {

        private SubModel model;

        @Override
        public Draw3dCommand build() {
            return new Draw3dCommand(this);
        }

        public Builder model(final SubModel model) {
            this.model = model;
            return this;
        }
    }
}
