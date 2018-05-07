package org.homonoia.sw.physics;

import com.badlogic.gdx.math.Matrix4;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/04/2018
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class KinematicMotionState extends BaseMotionState {
    public KinematicMotionState(Matrix4 worldTransform) {
        super(worldTransform);
    }

    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(worldTransform);
    }
    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
    }
}
