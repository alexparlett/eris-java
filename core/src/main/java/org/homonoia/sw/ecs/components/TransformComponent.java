package org.homonoia.sw.ecs.components;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.homonoia.sw.ecs.core.Component;
import org.homonoia.sw.ecs.core.DefaultConstructor;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.physics.MotionState;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/04/2018
 */
@Data
@AllArgsConstructor(onConstructor = @__(@DefaultConstructor))
public class TransformComponent extends Component {
    private MotionState motionState;

    @Override
    protected void addedToEntity(Entity entity) {
        motionState.obtain();
    }

    @Override
    protected void removedFromEntity() {
        motionState.release();
        motionState = null;
    }
}
