package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;

import static org.lwjgl.opengl.GL11.glEnable;

/**
 * Created by alexparlett on 13/02/2016.
 */
public class EnableCommand extends RenderCommand {

    private final int capability;

    protected EnableCommand(final Builder builder) {
        super(builder);
        this.capability = builder.capability;
    }

    @Override
    public void process(final Renderer renderer, final RenderKey renderKey) {
        glEnable(capability);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder extends RenderCommandBuilder<Builder> {

        private int capability;

        public EnableCommand build() {
            return new EnableCommand(this);
        }

        public Builder capability(final int capability) {
            this.capability = capability;
            return this;
        }
    }
}
