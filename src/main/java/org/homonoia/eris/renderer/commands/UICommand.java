package org.homonoia.eris.renderer.commands;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.ui.UI;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 18/04/2017
 */
public class UICommand extends RenderCommand {

    private static final Pool<UICommand> POOL = new ExpandingPool<>(16, Integer.MAX_VALUE, () -> new UICommand());

    @Override
    public void process(Renderer renderer, UI ui, RenderKey renderKey) {
        ui.render();
    }

    @Override
    public void free() {
        POOL.free(this);
    }

    public static UICommand newInstance() {
        return POOL.obtain();
    }
}
