package org.homonoia.sw.ecs.components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.homonoia.sw.ecs.core.Component;
import org.homonoia.sw.ecs.core.DefaultConstructor;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ModelComponent extends Component {
    private ModelInstance model;
    private BoundingBox boundingBox = new BoundingBox();

    @DefaultConstructor
    public ModelComponent(ModelInstance model) {
        this.model = model;
        this.model.calculateBoundingBox(this.boundingBox);
    }

    @Override
    public void dispose() {
        this.model = null;
        this.boundingBox = null;
    }
}
