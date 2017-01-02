package org.homonoia.eris.ecs.systems.render;

import org.homonoia.eris.ecs.Entity;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.ecs.components.Mesh;
import org.homonoia.eris.ecs.components.Transform;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.homonoia.eris.ecs.exceptions.RenderingException;
import org.homonoia.eris.graphics.drawables.model.SubModel;
import org.homonoia.eris.renderer.RenderFrame;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.commands.CameraCommand;
import org.homonoia.eris.renderer.commands.ClearColorCommand;
import org.homonoia.eris.renderer.commands.ClearCommand;
import org.homonoia.eris.renderer.commands.DrawModelCommand;
import org.homonoia.eris.renderer.commands.DrawSkyboxCommand;
import org.homonoia.eris.renderer.commands.EnableCommand;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/11/2016
 */
public final class CameraSceneParser implements Callable<Boolean> {

    private final RenderFrame renderFrame;
    private final Family renderableFamily;
    private final Entity cameraEntity;

    public CameraSceneParser(RenderFrame renderFrame, Family renderableFamily, Entity cameraEntity) {
        this.renderFrame = renderFrame;
        this.renderableFamily = renderableFamily;
        this.cameraEntity = cameraEntity;
    }

    @Override
    public Boolean call() throws RenderingException, MissingRequiredComponentException {
        Transform transform = cameraEntity.get(Transform.class).get();
        Camera camera = cameraEntity.get(Camera.class).get();

        renderFrame.add(ClearCommand.newInstance()
                .bitfield(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
                .renderKey(RenderKey.builder()
                        .target(camera.getRenderTarget().getHandle())
                        .targetLayer(0)
                        .command(1)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        renderFrame.add(ClearColorCommand.newInstance()
                .color(camera.getBackgroundColor())
                .renderKey(RenderKey.builder()
                        .target(camera.getRenderTarget().getHandle())
                        .targetLayer(0)
                        .command(0)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        renderFrame.add(EnableCommand.newInstance()
                .capability(GL_DEPTH_TEST)
                .renderKey(RenderKey.builder()
                        .target(camera.getRenderTarget().getHandle())
                        .targetLayer(0)
                        .command(2)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        Matrix4f view = transform.get().invert(new Matrix4f());
        float aspectRatio = (float) camera.getRenderTarget().getWidth() / camera.getRenderTarget().getHeight();
        Matrix4f perspective = new Matrix4f().identity().perspective(camera.getFov(), aspectRatio, camera.getNear(), camera.getFar());
        renderFrame.add(CameraCommand.newInstance()
                .view(view)
                .projection(perspective)
                .renderKey(RenderKey.builder()
                        .target(camera.getRenderTarget().getHandle())
                        .targetLayer(0)
                        .command(3)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));


        Matrix4f frustum = new Matrix4f().perspective(camera.getFov(), aspectRatio, camera.getNear(), camera.getFar())
                .translate(transform.getTranslation())
                .rotate(transform.getRotation());

        FrustumIntersection intersection = new FrustumIntersection(frustum);

        renderableFamily.getEntities()
                .parallelStream()
                .filter(filterEntities(camera, intersection))
                .spliterator()
                .forEachRemaining(processEntity(transform, camera));

        if (nonNull(camera.getSkybox())) {
            renderFrame.add(DrawSkyboxCommand.newInstance()
                    .skybox(camera.getSkybox())
                    .renderKey(RenderKey.builder()
                            .target(camera.getRenderTarget().getHandle())
                            .targetLayer(0)
                            .command(5)
                            .extra(0)
                            .depth(0)
                            .material(camera.getSkybox().getMaterial().getHandle())
                            .build()));
        }

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
        return subModel -> renderFrame.add(DrawModelCommand.newInstance()
                .model(subModel)
                .transform(rndrTransform.get())
                .renderKey(buildRenderKey(transform, camera, rndrTransform, subModel)));
    }

    protected RenderKey buildRenderKey(Transform transform, Camera camera, Transform rndrTransform, SubModel subModel) {
        return RenderKey.builder()
                .command(4)
                .material(subModel.getMaterial().getHandle())
                .targetLayer(rndrTransform.getLayer())
                .target(camera.getRenderTarget().getHandle())
                .transparency(0)
                .depth((long) rndrTransform.getTranslation().distance(transform.getTranslation()))
                .build();
    }

    protected Predicate<Entity> filterEntities(Camera camera, FrustumIntersection intersection) {
        return entity -> {
            Transform rndrTransform = entity.get(Transform.class).get();
            boolean inLayer = camera.getLayerMask().isEmpty() || camera.getLayerMask().contains(rndrTransform.getLayer());
            boolean visible = inLayer && intersection.testPoint(rndrTransform.getTranslation());
            return visible;
        };
    }
}
