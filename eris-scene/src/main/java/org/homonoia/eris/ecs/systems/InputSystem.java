package org.homonoia.eris.ecs.systems;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Statistics;
import org.homonoia.eris.core.exceptions.ErisException;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.ecs.FamilyManager;
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
    private final Statistics statistics;

    public InputSystem(Context context, final FamilyManager familyManager) {
        super(context, familyManager, MAX_PRIORITY);
        this.input = context.getBean(Input.class);
        statistics = context.getBean(Statistics.class);
    }

    @Override
    public void update(Update update) throws ErisException {
        statistics.getCurrent().startSegment();
        input.update(update.getTimeStep());
        statistics.getCurrent().endSegment("Input");
    }
}
