package org.homonoia.sw.ecs.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
public class CameraComponent extends Component {
    private final Viewport viewport;
    @DefaultConstructor
    public CameraComponent(Camera camera, int viewportWidth, int viewportHeight) {
        viewport = new ScreenViewport(camera);
        viewport.update(viewportWidth, viewportHeight, false);
    }
}
