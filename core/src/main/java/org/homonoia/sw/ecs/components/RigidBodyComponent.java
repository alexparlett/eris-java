package org.homonoia.sw.ecs.components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.homonoia.sw.ecs.core.Component;
import org.homonoia.sw.ecs.core.DefaultConstructor;
import org.homonoia.sw.physics.BaseMotionState;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RigidBodyComponent extends Component {
    private btRigidBody rigidBody;

    @DefaultConstructor
    public RigidBodyComponent(float mass, ModelInstance modelInstance, BaseMotionState motionState) {
        this.rigidBody = new btRigidBody(mass, motionState, Bullet.obtainStaticNodeShape(modelInstance.nodes));
        this.rigidBody.obtain();
    }

    @Override
    public void dispose() {
        rigidBody.release();
    }
}
