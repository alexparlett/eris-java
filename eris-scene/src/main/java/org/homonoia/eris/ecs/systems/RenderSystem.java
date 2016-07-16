package org.homonoia.eris.ecs.systems;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.events.frame.Update;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2016
 */
public class RenderSystem extends EntitySystem {

    private Family cameraFamily;

    public RenderSystem(final Context context) {
        super(context, MIN_PRIORITY);
        this.cameraFamily = familyManager.get(Camera.class);
    }

    @Override
    public void update(final Update update) {

    }
}
