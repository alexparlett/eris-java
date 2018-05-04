package org.homonoia.sw.ecs.components;

import com.badlogic.gdx.graphics.Camera;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.homonoia.sw.ecs.core.Component;
import org.homonoia.sw.ecs.core.DefaultConstructor;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 29/04/2018
 */
@Data
@AllArgsConstructor(onConstructor = @__(@DefaultConstructor))
public class CameraComponent extends Component {
    private Camera camera;
}
