package org.homonoia.eris.ecs.systems.render;

import org.homonoia.eris.ecs.Entity;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.ecs.components.Mesh;
import org.homonoia.eris.ecs.components.Transform;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.homonoia.eris.ecs.exceptions.RenderingException;
import org.homonoia.eris.graphics.drawables.model.AxisAlignedBoundingBox;
import org.homonoia.eris.graphics.drawables.model.SubModel;
import org.homonoia.eris.renderer.DebugMode;
import org.homonoia.eris.renderer.RenderFrame;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.commands.CameraCommand;
import org.homonoia.eris.renderer.commands.ClearColorCommand;
import org.homonoia.eris.renderer.commands.ClearCommand;
import org.homonoia.eris.renderer.commands.DrawBoundingSphereCommand;
import org.homonoia.eris.renderer.commands.DrawModelCommand;
import org.homonoia.eris.renderer.commands.DrawSkyboxCommand;
import org.homonoia.eris.renderer.commands.EnableCommand;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    private ThreadLocal<Vector3f> aabbMin = ThreadLocal.withInitial(() -> new Vector3f());
    private ThreadLocal<Vector3f> aabbMax = ThreadLocal.withInitial(() -> new Vector3f());
    private final RenderFrame renderFrame;
    private final Family renderableFamily;
    private final Entity cameraEntity;
    private final DebugMode debugMode;

    public CameraSceneParser(RenderFrame renderFrame, Family renderableFamily, Entity cameraEntity, DebugMode debugMode) {
        this.renderFrame = renderFrame;
        this.renderableFamily = renderableFamily;
        this.cameraEntity = cameraEntity;
        this.debugMode = debugMode;
    }

    @Override
    public Boolean call() throws RenderingException, MissingRequiredComponentException {
        Transform cameraTransform = cameraEntity.get(Transform.class).get();
        Camera camera = cameraEntity.get(Camera.class).get();

        renderFrame.add(ClearCommand.newInstance()
                .bitfield(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
                .renderKey(RenderKey.builder()
                        .target(camera.getRenderTarget().getHandle())
                        .targetLayer(0)
                        .command(0)
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
                        .command(0)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        Matrix4f view = camera.getViewMatrix();
        Matrix4f perspective = camera.getProjectionMatrix();
        renderFrame.add(CameraCommand.newInstance()
                .view(view)
                .projection(perspective)
                .renderKey(RenderKey.builder()
                        .target(camera.getRenderTarget().getHandle())
                        .targetLayer(0)
                        .command(0)
                        .extra(0)
                        .depth(0)
                        .material(0)
                        .build()));

        Matrix4f frustumMatrix = perspective.mul(view);
        FrustumIntersection intersection = new FrustumIntersection(frustumMatrix);

        renderableFamily.getEntities()
                .stream()
                .filter(filterEntities(camera, intersection, cameraTransform))
                .forEach(processEntity(cameraTransform, camera));

        if (nonNull(camera.getSkybox())) {
            renderFrame.add(DrawSkyboxCommand.newInstance()
                    .skybox(camera.getSkybox())
                    .renderKey(RenderKey.builder()
                            .target(camera.getRenderTarget().getHandle())
                            .targetLayer(0)
                            .command(3)
                            .extra(0)
                            .depth(Integer.MAX_VALUE)
                            .material(camera.getSkybox().getMaterial().getHandle())
                            .build()));
        }

        if (debugMode.isGrid()) {
        }

        return true;
    }

    protected Consumer<Entity> processEntity(Transform cameraTransform, Camera camera) {
        return entity -> {
            Transform rndrTransform = entity.get(Transform.class).get();
            Mesh mesh = entity.get(Mesh.class).get();

            if (debugMode.isBoundingBoxes()) {
                renderFrame.add(DrawBoundingSphereCommand.newInstance()
                        .boundingBox(mesh.getModel().getAxisAlignedBoundingBox())
                        .transform(rndrTransform)
                        .model(debugMode.getBoundingBoxCube().getSubModels().get(0))
                        .renderKey(RenderKey.builder()
                                .target(camera.getRenderTarget().getHandle())
                                .targetLayer(rndrTransform.getLayer())
                                .command(2)
                                .transparency(1)
                                .depth((long) rndrTransform.getTranslation().distance(cameraTransform.getTranslation()))
                                .material(debugMode.getBoundingBoxCube().getSubModels().get(0).getMaterial().getHandle())
                                .build()));
            }

            mesh.getModel().getSubModels().spliterator()
                    .forEachRemaining(processSubModel(cameraTransform, camera, rndrTransform));
        };
    }

    protected Consumer<SubModel> processSubModel(Transform cameraTransform, Camera camera, Transform rndrTransform) {
        return subModel -> renderFrame.add(DrawModelCommand.newInstance()
                .model(subModel)
                .transform(rndrTransform)
                .renderKey(buildRenderKey(cameraTransform, camera, rndrTransform, subModel)));
    }

    protected RenderKey buildRenderKey(Transform cameraTransform, Camera camera, Transform rndrTransform, SubModel subModel) {
        return RenderKey.builder()
                .command(2)
                .material(subModel.getMaterial().getHandle())
                .targetLayer(rndrTransform.getLayer())
                .target(camera.getRenderTarget().getHandle())
                .transparency(0)
                .depth((long) rndrTransform.getTranslation().distance(cameraTransform.getTranslation()))
                .build();
    }

    protected Predicate<Entity> filterEntities(Camera camera, FrustumIntersection intersection, Transform cameraTransform) {
        return entity -> {
            Transform rndrTransform = entity.get(Transform.class).get();
            Mesh rndrMesh = entity.get(Mesh.class).get();

            boolean inLayer = camera.getLayerMask().isEmpty() || camera.getLayerMask().contains(rndrTransform.getLayer());
            AxisAlignedBoundingBox aabb = rndrMesh.getModel().getAxisAlignedBoundingBox();
            boolean visible = inLayer
                    && testFrustumSphere(rndrTransform, aabb, intersection);
//                    && testNearPlane(rndrTransform, aabb, cameraTransform, camera);
            return visible;
        };
    }

    protected boolean testFrustumSphere(Transform rndrTransform, AxisAlignedBoundingBox aabb, FrustumIntersection intersection) {
        float modelExtent = aabb.getMin().distance(aabb.getMax());
        Vector3f translation = rndrTransform.getTranslation();
        return intersection.testSphere(translation, modelExtent);
    }

    protected boolean testNearPlane(Transform rndrTransform, AxisAlignedBoundingBox aabb, Transform cameraTransform, Camera camera) {
        float modelExtent = aabb.getMin().distance(aabb.getMax()) / 2;
        return (rndrTransform.getTranslation().length() - modelExtent) - cameraTransform.getTranslation().length() > camera.getNear();
    }
}
