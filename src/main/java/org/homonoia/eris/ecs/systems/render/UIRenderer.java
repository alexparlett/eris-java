package org.homonoia.eris.ecs.systems.render;

import org.homonoia.eris.renderer.RenderFrame;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.commands.UICommand;
import org.homonoia.eris.ui.UI;

import java.util.concurrent.Callable;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 18/04/2017
 */
public class UIRenderer implements Callable<Boolean> {

    private final RenderFrame renderFrame;
    private final UI ui;

    public UIRenderer(RenderFrame renderFrame, UI ui) {
        this.renderFrame = renderFrame;
        this.ui = ui;
    }

    @Override
    public Boolean call() throws Exception {
        ui.getRoot().layout();

        renderFrame.add(UICommand.newInstance()
                .renderKey(RenderKey.builder()
                        .target(0)
                        .targetLayer(0)
                        .command(5)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        return true;
    }
}
