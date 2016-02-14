package org.homonoia.eris.renderer;

/**
 * Created by alexparlett on 06/02/2016.
 */
public abstract class RenderCommand {

    private RenderKey renderKey;

    protected RenderCommand(RenderCommandBuilder<?> builder) {
        this.renderKey = builder.renderKey;
    }

    public RenderKey getRenderKey() {
        return renderKey;
    }

    public abstract void process(final Renderer renderer, final RenderKey renderKey);

    public static class RenderCommandBuilder<T extends RenderCommandBuilder> {

        private RenderKey renderKey;

        public T renderKey(final RenderKey renderKey) {
            this.renderKey = renderKey;
            return (T) this;
        }
    }
}