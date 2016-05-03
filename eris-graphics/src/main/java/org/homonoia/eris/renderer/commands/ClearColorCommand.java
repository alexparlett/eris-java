package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.math.Vector4f;
import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;

import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public class ClearColorCommand extends RenderCommand {

    private final Vector4f color;

    protected ClearColorCommand(final Builder builder) {
        super(builder);
        this.color = builder.color;
    }

    @Override
    public void process(final Renderer renderer, final RenderKey renderKey) {
        glClearColor(color.x, color.y, color.z, color.w);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends RenderCommandBuilder<Builder> {

        private Vector4f color;

        @Override
        public ClearColorCommand build() {
            return new ClearColorCommand(this);
        }

        public Builder color(final Vector4f color) {
            this.color = color;
            return this;
        }
    }
}
