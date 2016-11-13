package org.homonoia.eris.ecs.systems;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.exceptions.ErisException;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.events.frame.Update;
import org.homonoia.eris.input.Input;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/11/2016
 */
public class InputSystem extends EntitySystem {

    private final Input input;

    public InputSystem(Context context) {
        super(context, EntitySystem.MAX_PRIORITY);
        this.input = context.getBean(Input.class);
    }

    @Override
    public void update(Update update) throws ErisException {
        input.update();
    }
}
