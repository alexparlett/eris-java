package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.renderer.RenderState;
import org.homonoia.eris.renderer.Renderer;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class SwappingRenderState implements RenderState<CommandRenderFrame> {

    private final Pool<CommandRenderFrame> renderFramePool;
    private final Queue<CommandRenderFrame> frames = new ArrayBlockingQueue<>(128);
    private CommandRenderFrame currentFrame;

    public SwappingRenderState(Renderer renderer) {
        renderFramePool = new ExpandingPool<>(4, 8, new CommandRenderFrameFactory(renderer));
    }

    @Override
    public void add(final CommandRenderFrame renderFrame) {
        try {
            frames.add(renderFrame.sort());
            while(frameCount() > 2);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void process() {
        CommandRenderFrame lastFrame = currentFrame;
        currentFrame = frames.poll();

        if (nonNull(currentFrame)) {
            currentFrame.process();
            if (nonNull(lastFrame)) {
                renderFramePool.free(lastFrame);
            }
        } else if (nonNull(lastFrame)) {
            lastFrame.process();
        }
    }

    @Override
    public int frameCount() {
        return frames.size();
    }

    @Override
    public CommandRenderFrame newRenderFrame() {
        return renderFramePool.obtain().clear();
    }
}
