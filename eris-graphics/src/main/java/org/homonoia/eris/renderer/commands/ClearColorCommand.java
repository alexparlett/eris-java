package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Created by alexparlett on 13/02/2016.
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

    public static Builder builder() { return new Builder(); }

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
