package org.homonoia.eris.renderer.impl;

import org.homonoia.eris.renderer.RenderCommand;
import org.homonoia.eris.renderer.RenderFrame;
import org.homonoia.eris.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class CommandRenderFrame implements RenderFrame<CommandRenderFrame> {

    private final List<RenderCommand> renderCommands = new ArrayList<>();
    private final Renderer renderer;

    public CommandRenderFrame(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public CommandRenderFrame add(final RenderCommand renderCommand) {
        renderCommands.add(renderCommand);
        return this;
    }

    @Override
    public CommandRenderFrame sort() {
        renderCommands.sort(comparing(o -> o.getRenderKey().getKey()));
        return this;
    }

    @Override
    public CommandRenderFrame process() {
        if (!renderCommands.isEmpty()) {
            RenderCommand last = renderCommands.get(0);
            last.process(renderer, null);

            for (int i = 1; i < renderCommands.size(); ++i) {
                RenderCommand current = renderCommands.get(i);
                current.process(renderer, last.getRenderKey());
                last = current;
            }
        }

        return this;
    }

    @Override
    public CommandRenderFrame clear() {
        renderCommands.forEach(RenderCommand::free);
        renderCommands.clear();
        return this;
    }
}
