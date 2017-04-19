package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.pools.ExpandingPool;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.RenderState;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.renderer.commands.ClearColorCommand;
import org.homonoia.eris.renderer.commands.ClearCommand;
import org.homonoia.eris.ui.UI;
import org.joml.Vector4f;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

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

    private final Pool<CommandRenderFrame> renderFramePool;
    private final Queue<CommandRenderFrame> frames = new ArrayDeque<>(8);
    private CommandRenderFrame lastFrame;

    public SwappingRenderState(Renderer renderer, UI ui) {
        renderFramePool = new ExpandingPool<>(4, 8, new CommandRenderFrameFactory(renderer, ui));
        addDefaultFrame();
    }

    @Override
    public void add(final CommandRenderFrame renderFrame) {
        frames.add(renderFrame.sort());
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
        frames.forEach(commandRenderFrame -> renderFramePool.free(commandRenderFrame));
        frames.clear();
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
                .color(new Vector4f(0, 0, 0, 1))
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
