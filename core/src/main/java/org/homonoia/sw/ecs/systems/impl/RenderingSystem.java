package org.homonoia.sw.ecs.systems.impl;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import org.homonoia.sw.ecs.components.CameraComponent;
import org.homonoia.sw.ecs.components.ModelComponent;
import org.homonoia.sw.ecs.components.PointLightComponent;
import org.homonoia.sw.ecs.core.ComponentMapper;
import org.homonoia.sw.ecs.core.Engine;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.ecs.core.EntitySystem;
import org.homonoia.sw.ecs.core.Family;
import org.homonoia.sw.collections.ImmutableArray;

import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
public class RenderingSystem extends EntitySystem {
    private final Family cameraFamily;
    private final Family renderableFamily;
    private final ModelBatch modelBatch = new ModelBatch();
    private final Environment environment;
    private final DebugDrawer debugDrawer;
    private final Family lightsFamily;
    private final ComponentMapper<CameraComponent> cameraComponentMapper;
    private final ComponentMapper<PointLightComponent> lightComponentMapper;
    private final ComponentMapper<ModelComponent> modelComponentMapper;

    private ImmutableArray<Entity> cameraEntities;
    private ImmutableArray<Entity> renderableEntities;
    private ImmutableArray<Entity> lightsEntities;

    public RenderingSystem(final Environment environment, final DebugDrawer debugDrawer) {
        super(MIN_PRIORITY);
        this.environment = environment;
        this.debugDrawer = debugDrawer;
        renderableFamily = Family.all(ModelComponent.class).get();
        cameraFamily = Family.all(CameraComponent.class).get();
        lightsFamily = Family.one(PointLightComponent.class).get();
        modelComponentMapper = ComponentMapper.getFor(ModelComponent.class);
        cameraComponentMapper = ComponentMapper.getFor(CameraComponent.class);
        lightComponentMapper = ComponentMapper.getFor(PointLightComponent.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        renderableEntities = engine.getEntitiesFor(renderableFamily);
        cameraEntities = engine.getEntitiesFor(cameraFamily);
        lightsEntities = engine.getEntitiesFor(lightsFamily);
    }

    @Override
    public void update(float deltaTime) {
        lightsEntities.stream()
                .map(lightComponentMapper::get)
                .map(PointLightComponent::getPointLight)
                .filter(Objects::nonNull)
                .forEachOrdered(environment::add);

        cameraEntities.stream()
                .map(cameraComponentMapper::get)
                .forEachOrdered(cameraComponent -> {
                    modelBatch.begin(cameraComponent.getViewport().getCamera());

                    renderableEntities.stream()
                            .map(modelComponentMapper::get)
                            .filter(Objects::nonNull)
                            .filter(modelComponent -> isVisible(cameraComponent,modelComponent))
                            .map(ModelComponent::getModel)
                            .forEachOrdered(modelInstance -> modelBatch.render(modelInstance, environment));

                    modelBatch.end();

                    if (nonNull(debugDrawer) && debugDrawer.getDebugMode() > 0) {
                        debugDrawer.begin(cameraComponent.getViewport());
                        getEngine().getSystem(PhysicsSystem.class).getWorld().debugDrawWorld();
                        debugDrawer.end();
                    }
                });
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        debugDrawer.dispose();
    }

    private boolean isVisible(CameraComponent cameraComponent, ModelComponent modelComponent) {
        return cameraComponent.getViewport().getCamera().frustum.boundsInFrustum(modelComponent.getBoundingBox().mul(modelComponent.getModel().transform));
    }

}
