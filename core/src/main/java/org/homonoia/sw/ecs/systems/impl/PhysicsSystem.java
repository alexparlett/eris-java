package org.homonoia.sw.ecs.systems.impl;

import org.homonoia.sw.ecs.components.RigidBodyComponent;
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
public class PhysicsSystem extends EntitySystem {
    private Family physicsFamily;
    private ImmutableArray<Entity> physicsEntities;

    public PhysicsSystem() {
        super(Integer.MAX_VALUE);
        physicsFamily = Family.all(RigidBodyComponent.class).get();
    }

    @Override
    public void addedToEngine(Engine engine) {
        physicsEntities = engine.getEntitiesFor(physicsFamily);
    }

    @Override
    public void removedFromEngine(Engine engine) {

    }

    @Override
    public void update(float deltaTime) {
    }
}
