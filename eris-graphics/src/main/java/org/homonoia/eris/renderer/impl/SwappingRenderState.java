package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.renderer.RenderQueue;
import org.homonoia.eris.renderer.RenderState;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.renderer.RenderCommand;

/**
 * Created by alexparlett on 06/02/2016.
 */
public class SwappingRenderState implements RenderState {

    private final RenderQueue[] renderQueues;
    private int renderQueue = 0;
    private int updateQueue = 1;

    public SwappingRenderState(final Renderer renderer) {
        renderQueues = new RenderQueue[] {
            new SynchronizedRenderQueue(renderer),
            new SynchronizedRenderQueue(renderer),
            new SynchronizedRenderQueue(renderer)
        };
    }

    @Override
    public void add(final RenderCommand renderCommand) {
        renderQueues[updateQueue].add(renderCommand);
    }

    @Override
    public void swap() {
        renderQueue = updateQueue++;
        if (updateQueue >= renderQueues.length) {
            updateQueue = 0;
        }

        renderQueues[updateQueue].clear();
        renderQueues[renderQueue].sort();
    }

    @Override
    public void process() {
        renderQueues[renderQueue].process();
    }
}
