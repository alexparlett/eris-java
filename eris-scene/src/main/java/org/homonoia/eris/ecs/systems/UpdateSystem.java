package org.homonoia.eris.ecs.systems;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Statistics;
import org.homonoia.eris.core.exceptions.ErisException;
import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.FamilyManager;
import org.homonoia.eris.events.frame.Update;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/11/2016
 */
public class UpdateSystem extends EntitySystem {


    private final Family family;
    private final Statistics statistics;

    public UpdateSystem(Context context, final FamilyManager familyManager) {
        super(context, familyManager, MAX_PRIORITY + 10);
        this.family = familyManager.get(Component.class);
        statistics = context.getBean(Statistics.class);
    }

    @Override
    public void update(Update update) throws ErisException {
        statistics.getCurrent().startSegment();
        family.getEntities().stream()
                .flatMap(entity -> entity.getAll().stream())
                .forEach(component -> component.update(update.getTimeStep()));
        statistics.getCurrent().endSegment("Component Update");
    }
}
