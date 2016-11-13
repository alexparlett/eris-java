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
import org.homonoia.eris.renderer.commands.Draw3dCommand;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

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
                        .target(isNull(camera.getRenderTarget()) ? 0 : camera.getRenderTarget().getHandle())
                        .targetLayer(0)
                        .command(0)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        renderFrame.add(ClearColorCommand.newInstance()
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
        renderFrame.add(CameraCommand.newInstance()
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
        return subModel -> renderFrame.add(Draw3dCommand.newInstance()
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
