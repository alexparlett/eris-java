package org.homonoia.sw.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/04/2018
 */
@AllArgsConstructor
public class BaseMotionState extends btMotionState {
    @Getter
    protected Matrix4 worldTransform;

    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(worldTransform);
    }
    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        worldTransform = worldTransform.set(worldTrans);
    }
}
