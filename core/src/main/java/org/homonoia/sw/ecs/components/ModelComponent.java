package org.homonoia.sw.ecs.components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import lombok.Data;
import org.homonoia.sw.ecs.core.Component;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
@Data
public class ModelComponent extends Component {
    private ModelInstance model;
}
