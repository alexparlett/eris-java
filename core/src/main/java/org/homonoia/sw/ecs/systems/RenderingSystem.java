package org.homonoia.sw.ecs.systems;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import org.homonoia.sw.ecs.components.CameraComponent;
import org.homonoia.sw.ecs.components.ModelComponent;
import org.homonoia.sw.ecs.components.TransformComponent;
import org.homonoia.sw.ecs.core.Engine;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.ecs.core.EntitySystem;
import org.homonoia.sw.ecs.core.Family;
import org.homonoia.sw.ecs.utils.ImmutableArray;

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
    private ImmutableArray<Entity> renderableEntities;
    private ImmutableArray<Entity> cameraEntities;
    private ModelBatch modelBatch = new ModelBatch();

    public RenderingSystem() {
        super(Integer.MAX_VALUE);
        renderableFamily = Family.all(ModelComponent.class, TransformComponent.class).get();
        cameraFamily = Family.all(CameraComponent.class, TransformComponent.class).get();
    }

    @Override
    public void addedToEngine(Engine engine) {
        renderableEntities = engine.getEntitiesFor(renderableFamily);
        cameraEntities = engine.getEntitiesFor(cameraFamily);
    }

    @Override
    public void update(float deltaTime) {

        cameraEntities.stream()
                .forEachOrdered(entity -> {
                    CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);
                    modelBatch.begin(cameraComponent.getCamera());

                    renderableEntities.stream()
                            .filter(renderEntity -> isVisible(cameraComponent, entity.getComponent(TransformComponent.class)))
                            .map(renderEntity -> renderEntity.getComponent(ModelComponent.class).getModel())
                            .forEachOrdered(modelBatch::render);

                    modelBatch.end();

                });
    }

    private boolean isVisible(CameraComponent cameraComponent, TransformComponent transformComponent) {
        transformComponent.getTransform().getTranslation(position);
        return cameraComponent.getCamera().frustum.pointInFrustum(position);
    }

}
