package org.homonoia.sw.ecs.components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import lombok.Data;
import org.homonoia.sw.ecs.core.Component;
import org.homonoia.sw.ecs.core.DefaultConstructor;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.physics.MotionState;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
@Data
public class RigidBodyComponent extends Component {
    private btRigidBody rigidBody;

    @DefaultConstructor
    public RigidBodyComponent(float mass, ModelInstance modelInstance, MotionState motionState) {
        rigidBody = new btRigidBody(mass, motionState, Bullet.obtainStaticNodeShape(modelInstance.nodes));
    }

    @Override
    protected void addedToEntity(Entity entity) {
        rigidBody.obtain();
    }

    @Override
    protected void removedFromEntity() {
        rigidBody.release();
        rigidBody = null;
    }
}
