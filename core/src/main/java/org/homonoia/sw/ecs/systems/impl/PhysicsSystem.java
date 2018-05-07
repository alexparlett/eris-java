package org.homonoia.sw.ecs.systems.impl;

import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import lombok.Getter;
import org.homonoia.sw.ecs.components.RigidBodyComponent;
import org.homonoia.sw.ecs.core.ComponentMapper;
import org.homonoia.sw.ecs.core.Engine;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.ecs.core.EntityListener;
import org.homonoia.sw.ecs.core.EntitySystem;
import org.homonoia.sw.ecs.core.Family;
import org.homonoia.sw.ecs.signals.ComponentRemovedSignal;

import static java.util.Objects.nonNull;
import static org.homonoia.sw.mvc.config.AutumnActionPriority.MIN_PRIORITY;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
@Getter
public class PhysicsSystem extends EntitySystem implements EntityListener  {
    private final btBroadphaseInterface broadphase;
    private final btCollisionDispatcher dispatcher;
    private final btDefaultCollisionConfiguration collisionConfig;
    private final btConstraintSolver solver;
    private final btDynamicsWorld world;
    private final Family family;
    private final ComponentMapper<RigidBodyComponent> rigidBodyComponentMapper;

    public PhysicsSystem(DebugDrawer debugDrawer) {
        super(MIN_PRIORITY);
        this.family = Family.all(RigidBodyComponent.class).get();
        this.collisionConfig = new btDefaultCollisionConfiguration();
        this.dispatcher = new btCollisionDispatcher(collisionConfig);
        this.broadphase = new btDbvtBroadphase();
        this.solver = new btSequentialImpulseConstraintSolver();
        this.world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);
        this.world.setDebugDrawer(debugDrawer);
        rigidBodyComponentMapper = ComponentMapper.getFor(RigidBodyComponent.class);
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
    public void update(float deltaTime) {
        world.stepSimulation(deltaTime, 5, 1/60.f);
    }

    @Override
    public void dispose() {
        world.dispose();
        collisionConfig.dispose();
        dispatcher.dispose();
        broadphase.dispose();
        solver.dispose();
    }

    @Override
    public void entityAdded(Entity entity) {
        RigidBodyComponent component = rigidBodyComponentMapper.get(entity);
        if (nonNull(component)) {
            world.addRigidBody(component.getRigidBody());

            entity.publisher.filter(ComponentRemovedSignal.class::isInstance)
                    .map(ComponentRemovedSignal.class::cast)
                    .map(ComponentRemovedSignal::getComponent)
                    .filter(RigidBodyComponent.class::isInstance)
                    .map(RigidBodyComponent.class::cast)
                    .map(RigidBodyComponent::getRigidBody)
                    .subscribe(world::removeRigidBody);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        RigidBodyComponent component = rigidBodyComponentMapper.get(entity);
        if (nonNull(component)) {
            world.removeRigidBody(component.getRigidBody());
        }
    }
}
