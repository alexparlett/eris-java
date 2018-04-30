package org.homonoia.sw.ecs.components;

import com.badlogic.gdx.math.Matrix4;
import lombok.Data;
import org.homonoia.sw.ecs.core.Component;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
@Data
public class TransformComponent extends Component {
    private Matrix4 transform;
}
