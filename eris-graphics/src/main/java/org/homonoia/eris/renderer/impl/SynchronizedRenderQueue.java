package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderQueue;
import org.homonoia.eris.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class SynchronizedRenderQueue implements RenderQueue {

    private final List<RenderCommand> renderCommands = new ArrayList<>();
    private final Renderer renderer;

    public SynchronizedRenderQueue(final Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public synchronized void add(final RenderCommand renderCommand) {
        renderCommands.add(renderCommand);
    }

    @Override
    public synchronized void sort() {
        renderCommands.sort((o1, o2) -> o1.getRenderKey().getKey().compareTo(o2.getRenderKey().getKey()));
    }

    @Override
    public synchronized void process() {
        if (renderCommands.isEmpty()) {
            return;
        }

        RenderCommand last = renderCommands.get(0);
        last.process(renderer, null);

        for (int i = 1; i < renderCommands.size(); ++i) {
            RenderCommand current = renderCommands.get(i);
            current.process(renderer, last.getRenderKey());
            last = current;
        }
    }

    @Override
    public synchronized void clear() {
        renderCommands.forEach(RenderCommand::free);
        renderCommands.clear();
    }

}
