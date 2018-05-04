package org.homonoia.sw.ecs.systems.impl;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import org.homonoia.sw.ecs.components.CameraComponent;
import org.homonoia.sw.ecs.components.ModelComponent;
import org.homonoia.sw.ecs.core.Engine;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.ecs.core.EntitySystem;
import org.homonoia.sw.ecs.core.Family;
import org.homonoia.sw.ecs.utils.ImmutableArray;

import java.util.Objects;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
public class RenderingSystem extends EntitySystem {
    private Family cameraFamily;
    private Family renderableFamily;
    private Vector3 position = new Vector3();
    private Vector3 dimensions = new Vector3();
    private ImmutableArray<Entity> renderableEntities;
    private ImmutableArray<Entity> cameraEntities;
    private ModelBatch modelBatch = new ModelBatch();
    private Environment environment = new Environment();

    public RenderingSystem() {
        super(Integer.MAX_VALUE);
        renderableFamily = Family.all(ModelComponent.class).get();
        cameraFamily = Family.all(CameraComponent.class).get();
    }

    @Override
    public void addedToEngine(Engine engine) {
        renderableEntities = engine.getEntitiesFor(renderableFamily);
        cameraEntities = engine.getEntitiesFor(cameraFamily);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        modelBatch.dispose();
    }

    @Override
    public void update(float deltaTime) {
        cameraEntities.stream()
                .forEachOrdered(entity -> {
                    CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);
                    modelBatch.begin(cameraComponent.getCamera());

                    renderableEntities.stream()
                            .map(renderEntity -> renderEntity.getComponent(ModelComponent.class))
                            .filter(Objects::nonNull)
                            .filter(modelComponent -> isVisible(cameraComponent,modelComponent))
                            .map(ModelComponent::getModel)
                            .forEachOrdered(modelInstance -> modelBatch.render(modelInstance));

                    modelBatch.end();

                });
    }

    private boolean isVisible(CameraComponent cameraComponent, ModelComponent modelComponent) {
        return cameraComponent.getCamera().frustum.boundsInFrustum(modelComponent.getBoundingBox().mul(modelComponent.getModel().transform));
    }

}
