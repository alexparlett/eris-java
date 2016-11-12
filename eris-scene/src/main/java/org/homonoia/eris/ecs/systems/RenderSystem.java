package org.homonoia.eris.ecs.systems;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.ecs.Entity;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.ecs.components.Mesh;
import org.homonoia.eris.ecs.components.Transform;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.homonoia.eris.ecs.exceptions.RenderingException;
import org.homonoia.eris.events.frame.Update;
import org.homonoia.eris.graphics.drawables.model.SubModel;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.renderer.commands.CameraCommand;
import org.homonoia.eris.renderer.commands.ClearColorCommand;
import org.homonoia.eris.renderer.commands.ClearCommand;
import org.homonoia.eris.renderer.commands.Draw3dCommand;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
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

    public RenderSystem(final Context context) {
        super(context, MIN_PRIORITY);
        this.completionService = new ExecutorCompletionService<>(context.getBean(ExecutorService.class));
        this.cameraFamily = familyManager.get(Camera.class);
        this.renderableFamily = familyManager.get(Mesh.class);
        this.renderer = this.getContext().getBean(Renderer.class);
    }

    @Override
    public void update(final Update update) throws RenderingException {
        if (cameraFamily.getEntities().isEmpty()) {
            renderer.getState().add(ClearCommand.newInstance()
                    .bitfield(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
                    .renderKey(RenderKey.builder()
                            .target(0)
                            .targetLayer(0)
                            .command(0)
                            .extra(0)
                            .depth(0)
                            .material(0)
                            .build()));

            renderer.getState().add(ClearColorCommand.newInstance()
                    .color(new Vector4f(0,0,0,1))
                    .renderKey(RenderKey.builder()
                            .target(0)
                            .targetLayer(0)
                            .command(1)
                            .extra(0)
                            .depth(0)
                            .material(0)
                            .build()));
        } else {
            long totalCount = cameraFamily.getEntities().stream()
                    .map(entity -> completionService.submit(new CameraSceneParser(renderer, renderableFamily, entity)))
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
            } catch (InterruptedException | ExecutionException e) {
                throw new RenderingException("Failed processing scene", e);
            }
        }

        renderer.getState().swap();
    }

    protected static final class CameraSceneParser implements Callable<Boolean> {

        private final Renderer renderer;
        private final Family renderableFamily;
        private final Entity cameraEntity;

        protected CameraSceneParser(Renderer renderer, Family renderableFamily, Entity cameraEntity) {
            this.renderer = renderer;
            this.renderableFamily = renderableFamily;
            this.cameraEntity = cameraEntity;
        }

        @Override
        public Boolean call() throws RenderingException, MissingRequiredComponentException {
            Transform transform = cameraEntity.get(Transform.class).get();
            Camera camera = cameraEntity.get(Camera.class).get();

            renderer.getState().add(ClearCommand.newInstance()
                    .bitfield(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
                    .renderKey(RenderKey.builder()
                            .target(isNull(camera.getRenderTarget()) ? 0 : camera.getRenderTarget().getHandle())
                            .targetLayer(0)
                            .command(0)
                            .extra(0)
                            .depth(0)
                            .material(0)
                            .build()));

            renderer.getState().add(ClearColorCommand.newInstance()
                    .color(camera.getBackgroundColor())
                    .renderKey(RenderKey.builder()
                            .target(isNull(camera.getRenderTarget()) ? 0 : camera.getRenderTarget().getHandle())
                            .targetLayer(0)
                            .command(1)
                            .extra(0)
                            .depth(0)
                            .material(0)
                            .build()));

            Matrix4f view = transform.get().invert(new Matrix4f());
            Matrix4f perspective = new Matrix4f().identity().perspective(camera.getFov(), camera.getAspect(), camera.getNear(), camera.getFar());
            renderer.getState().add(CameraCommand.newInstance()
                    .view(view)
                    .projection(perspective)
                    .renderKey(RenderKey.builder()
                            .target(isNull(camera.getRenderTarget()) ? 0 : camera.getRenderTarget().getHandle())
                            .targetLayer(0)
                            .command(2)
                            .extra(0)
                            .depth(0)
                            .material(0)
                            .build()));

            Matrix4f frustum = new Matrix4f().perspective(camera.getFov(), camera.getAspect(), camera.getNear(), camera.getFar())
                    .translate(transform.getTranslation())
                    .rotate(transform.getRotation());

            FrustumIntersection intersection = new FrustumIntersection(frustum);

            renderableFamily.getEntities()
                    .parallelStream()
                    .filter(filterEntities(transform, camera, intersection))
                    .spliterator()
                    .forEachRemaining(processEntity(transform, camera));

            return true;
        }

        protected Consumer<Entity> processEntity(Transform transform, Camera camera) {
            return entity -> {
                Transform rndrTransform = entity.get(Transform.class).get();
                Mesh mesh = entity.get(Mesh.class).get();

                mesh.getModel().getSubModels().spliterator()
                        .forEachRemaining(processSubModel(transform, camera, rndrTransform));
            };
        }

        protected Consumer<SubModel> processSubModel(Transform transform, Camera camera, Transform rndrTransform) {
            return subModel -> renderer.getState().add(Draw3dCommand.newInstance()
                    .material(subModel.getMaterial())
                    .model(subModel)
                    .transform(rndrTransform.get())
                    .renderKey(buildRenderKey(transform, camera, rndrTransform, subModel)));
        }

        protected RenderKey buildRenderKey(Transform transform, Camera camera, Transform rndrTransform, SubModel subModel) {
            return RenderKey.builder()
                    .command(3)
                    .material(subModel.getMaterial().getHandle())
                    .targetLayer(rndrTransform.getLayer())
                    .target(isNull(camera.getRenderTarget()) ? 0 : camera.getRenderTarget().getHandle())
                    .transparency(0)
                    .depth((long) rndrTransform.getTranslation().distance(transform.getTranslation()))
                    .build();
        }

        protected Predicate<Entity> filterEntities(Transform transform, Camera camera, FrustumIntersection intersection) {
            return entity -> {
                Transform rndrTransform = entity.get(Transform.class).get();
                boolean inLayer = camera.getLayerMask().isEmpty() || camera.getLayerMask().contains(rndrTransform.getLayer());
                return inLayer && intersection.testPoint(rndrTransform.getTranslation());
            };
        }
    }
}
