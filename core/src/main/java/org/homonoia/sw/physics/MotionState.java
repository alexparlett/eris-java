package org.homonoia.sw.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/04/2018
 */
@Data
@AllArgsConstructor
public class MotionState extends btMotionState {
    private Matrix4 worldTransform;

    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(worldTransform);
    }
    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        worldTransform.set(worldTrans);
    }
}
