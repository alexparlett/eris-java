package org.homonoia.eris.ecs.systems;

import lombok.Getter;
import lombok.Setter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.exceptions.ErisException;
import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.FamilyManager;
import org.homonoia.eris.ecs.components.RigidBody;
import org.homonoia.eris.events.frame.FixedUpdate;
import org.ode4j.ode.internal.DxWorld;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 21/04/2017
 */
@Getter
@Setter
public class PhysicsSystem extends EntitySystem {

    private final Family rigidBodyFamily;
    @Getter private DxWorld world;

    public PhysicsSystem(Context context, FamilyManager familyManager) {
        super(context, familyManager);
        this.world = DxWorld.dWorldCreate();
        this.rigidBodyFamily = familyManager.get(RigidBody.class);
        this.rigidBodyFamily.setAddedCallback(this::handleRigidBodyAdded);
        this.rigidBodyFamily.setRemovedCallback(this::handleRigidBodyRemoved);

    }

    @Override
    public void fixedUpdate(FixedUpdate update) throws ErisException {
        world.step(update.getTimeStep());
    }

    private void handleRigidBodyAdded(Component component) {
        RigidBody rigidBody = (RigidBody) component;
        rigidBody.create(world);
    }

    private void handleRigidBodyRemoved(Component component) {
        RigidBody rigidBody = (RigidBody) component;
        rigidBody.destroy();
    }
}
