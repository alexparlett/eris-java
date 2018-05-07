package org.homonoia.sw.ecs.components;

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
 * @since 30/04/2018
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TransformComponent extends Component {
    private BaseMotionState motionState;

    @DefaultConstructor
    public TransformComponent(BaseMotionState motionState) {
        this.motionState = motionState;
        this.motionState.obtain();
    }

    @Override
    public void dispose() {
        motionState.release();
    }
}
