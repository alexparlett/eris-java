package org.homonoia.sw.ecs.systems.impl;

import com.badlogic.gdx.graphics.g3d.Environment;
import lombok.Getter;
import org.homonoia.sw.ecs.components.PointLightComponent;
import org.homonoia.sw.ecs.components.RigidBodyComponent;
import org.homonoia.sw.ecs.core.ComponentMapper;
import org.homonoia.sw.ecs.core.Engine;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.ecs.core.EntityListener;
import org.homonoia.sw.ecs.core.EntitySystem;
import org.homonoia.sw.ecs.core.Family;
import org.homonoia.sw.ecs.signals.ComponentRemovedSignal;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
@Getter
public class EnvironmentSystem extends EntitySystem implements EntityListener  {
    private final Family family;
    private final ComponentMapper<PointLightComponent> pointLightComponentMapper;
    private final Environment environment;

    public EnvironmentSystem(Environment environment) {
        super(DEFAULT_PRIORITY);
        this.environment = environment;
        this.family = Family.all(RigidBodyComponent.class).get();
        this.pointLightComponentMapper = ComponentMapper.getFor(PointLightComponent.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(family, this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        PointLightComponent component = pointLightComponentMapper.get(entity);
        if (nonNull(component)) {
            environment.add(component.getPointLight());

            entity.publisher.filter(ComponentRemovedSignal.class::isInstance)
                    .map(ComponentRemovedSignal.class::cast)
                    .map(ComponentRemovedSignal::getComponent)
                    .filter(PointLightComponent.class::isInstance)
                    .map(PointLightComponent.class::cast)
                    .map(PointLightComponent::getPointLight)
                    .subscribe(environment::remove);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        PointLightComponent component = pointLightComponentMapper.get(entity);
        if (nonNull(component)) {
            environment.remove(component.getPointLight());
        }
    }
}
