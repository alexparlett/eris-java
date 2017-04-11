package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.RenderState;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.renderer.commands.ClearColorCommand;
import org.homonoia.eris.renderer.commands.ClearCommand;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.Objects.nonNull;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class SwappingRenderState implements RenderState<CommandRenderFrame> {

    private static final Logger LOG = LoggerFactory.getLogger(SwappingRenderState.class);

    private final Pool<CommandRenderFrame> renderFramePool;
    private final Queue<CommandRenderFrame> frames = new ArrayBlockingQueue<>(8);
    private CommandRenderFrame lastFrame;

    public SwappingRenderState(Renderer renderer) {
        renderFramePool = new ExpandingPool<>(4, 8, new CommandRenderFrameFactory(renderer));

        addDefaultFrame();
    }

    @Override
    public void add(final CommandRenderFrame renderFrame) {
        synchronized (frames) {
            frames.add(renderFrame.sort());
        }
        while (frameCount() > 4);
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
    public void clear() {
        synchronized (frames) {
            frames.forEach(commandRenderFrame -> renderFramePool.free(commandRenderFrame));
            frames.clear();
        }
        lastFrame = null;
        addDefaultFrame();
    }

    @Override
    public int frameCount() {
        return frames.size();
    }

    @Override
    public CommandRenderFrame newRenderFrame() {
        return renderFramePool.obtain().clear();
    }

    private void addDefaultFrame() {
        CommandRenderFrame renderFrame = newRenderFrame();
        renderFrame.add(ClearCommand.newInstance()
                .bitfield(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
                .renderKey(RenderKey.builder()
                        .target(0)
                        .targetLayer(0)
                        .command(1)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        renderFrame.add(ClearColorCommand.newInstance()
                .color(new Vector4f(0,0,0,1))
                .renderKey(RenderKey.builder()
                        .target(0)
                        .targetLayer(0)
                        .command(0)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));
        add(renderFrame);
    }
}
