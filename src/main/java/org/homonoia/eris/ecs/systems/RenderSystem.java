package org.homonoia.eris.ecs.systems;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Statistics;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.FamilyManager;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.ecs.components.Renderable;
import org.homonoia.eris.ecs.exceptions.RenderingException;
import org.homonoia.eris.ecs.systems.render.CameraRenderer;
import org.homonoia.eris.ecs.systems.render.UIRenderer;
import org.homonoia.eris.events.frame.Update;
import org.homonoia.eris.renderer.RenderFrame;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.renderer.commands.ClearCommand;
import org.homonoia.eris.ui.UI;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.Objects.nonNull;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2016
 */
public class RenderSystem extends EntitySystem {

    private final CompletionService<Boolean> completionService;
    private final Renderer renderer;
    private final Family cameraFamily;
    private final Family renderableFamily;
    private final Statistics statistics;
    private final UI ui;

    public RenderSystem(final Context context, final FamilyManager familyManager) {
        super(context, familyManager, MIN_PRIORITY - 100);
        this.completionService = new ExecutorCompletionService<>(context.getBean(ExecutorService.class));
        this.cameraFamily = familyManager.get(Camera.class);
        this.renderableFamily = familyManager.get(Renderable.class);
        this.renderer = this.getContext().getBean(Renderer.class);
        this.ui = this.getContext().getBean(UI.class);
        this.statistics = context.getBean(Statistics.class);
    }

    @Override
    public void update(final Update update) throws RenderingException {
        statistics.getCurrent().startSegment();
        RenderFrame renderFrame = renderer.getState().newRenderFrame();

        renderFrame.add(ClearCommand.newInstance()
                .bitfield(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
                .renderKey(RenderKey.builder()
                        .target(0)
                        .targetLayer(0)
                        .command(0)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        long totalCount = 1L;
        completionService.submit(new UIRenderer(renderFrame, ui));

        if (!cameraFamily.getEntities().isEmpty()) {
            totalCount += cameraFamily.getEntities().stream()
                    .map(entity -> completionService.submit(new CameraRenderer(renderFrame, renderableFamily, entity, renderer.getDebugMode())))
                    .count();
        }
        long currentCount = 0L;
        try {
            while (currentCount < totalCount) {
                Future<Boolean> take = completionService.take();
                if (nonNull(take)) {
                    take.get();
                    currentCount++;
                }
            }
        } catch (Exception e) {
            throw new RenderingException("Failed processing scene", e);
        }

        renderer.getState().add(renderFrame);
        statistics.getCurrent().endSegment("Render Preparation");
    }

}
