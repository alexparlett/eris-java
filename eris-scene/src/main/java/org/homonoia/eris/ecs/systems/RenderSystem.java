package org.homonoia.eris.ecs.systems;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Statistics;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.FamilyManager;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.ecs.components.Mesh;
import org.homonoia.eris.ecs.exceptions.RenderingException;
import org.homonoia.eris.ecs.systems.render.CameraSceneParser;
import org.homonoia.eris.events.frame.Update;
import org.homonoia.eris.renderer.RenderFrame;
import org.homonoia.eris.renderer.Renderer;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.Objects.nonNull;

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

    public RenderSystem(final Context context, final FamilyManager familyManager) {
        super(context, familyManager, MIN_PRIORITY);
        this.completionService = new ExecutorCompletionService<>(context.getBean(ExecutorService.class));
        this.cameraFamily = familyManager.get(Camera.class);
        this.renderableFamily = familyManager.get(Mesh.class);
        this.renderer = this.getContext().getBean(Renderer.class);
        this.statistics = context.getBean(Statistics.class);
    }

    @Override
    public void update(final Update update) throws RenderingException {
        statistics.getCurrent().startSegment();
        if (!cameraFamily.getEntities().isEmpty()) {
            RenderFrame renderFrame = renderer.getState().newRenderFrame();

            long totalCount = cameraFamily.getEntities().stream()
                    .map(entity -> completionService.submit(new CameraSceneParser(renderFrame, renderableFamily, entity, renderer.getDebugMode())))
                    .count();
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
        }
        statistics.getCurrent().endSegment("Scene Render");
    }

}
