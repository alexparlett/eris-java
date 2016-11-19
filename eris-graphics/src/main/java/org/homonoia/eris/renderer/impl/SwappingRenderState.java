package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.renderer.RenderState;
import org.homonoia.eris.renderer.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
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

    private static final Logger LOG = LoggerFactory.getLogger(SwappingRenderState.class);

    private final Pool<CommandRenderFrame> renderFramePool;
    private final Queue<CommandRenderFrame> frames = new ArrayBlockingQueue<>(128);
    private CommandRenderFrame lastFrame;

    public SwappingRenderState(Renderer renderer) {
        renderFramePool = new ExpandingPool<>(4, 8, new CommandRenderFrameFactory(renderer));
    }

    @Override
    public void add(final CommandRenderFrame renderFrame) {
        frames.add(renderFrame.sort());
        while (frameCount() > 2);
    }

    @Override
    public void process() {
        CommandRenderFrame currentFrame = frames.poll();

        if (Objects.equals(lastFrame, currentFrame)) {
            throw new RuntimeException();
        }

        if (nonNull(currentFrame)) {
            currentFrame.process();
            if (nonNull(lastFrame)) {
                renderFramePool.free(lastFrame);
            }
            lastFrame = currentFrame;
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
