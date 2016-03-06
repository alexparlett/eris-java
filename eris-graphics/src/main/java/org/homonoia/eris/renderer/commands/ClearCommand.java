package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;

import static org.lwjgl.opengl.GL11.glClear;

/**
 * Created by alexparlett on 13/02/2016.
 */
public class ClearCommand extends RenderCommand {

    private final int bitfield;

    protected ClearCommand(final Builder builder) {
        super(builder);
        this.bitfield = builder.bitfield;
    }

    @Override
    public void process(final Renderer renderer, final RenderKey renderKey) {
        glClear(bitfield);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder extends RenderCommandBuilder<Builder> {

        private int bitfield;

        @Override
        public ClearCommand build() {
            return new ClearCommand(this);
        }

        public Builder bitfield(final int bitfield) {
            this.bitfield = bitfield;
            return this;
        }
    }
}
