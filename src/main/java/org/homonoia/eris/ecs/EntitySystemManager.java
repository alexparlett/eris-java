package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.Statistics;
import org.homonoia.eris.core.exceptions.ErisException;
import org.homonoia.eris.events.frame.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by alexparlett on 30/05/2016.
 */
public class EntitySystemManager extends Contextual {

    private static final Logger LOG = LoggerFactory.getLogger(EntitySystemManager.class);
    private final Statistics statistics;

    private SortedSet<EntitySystem> entitySystems = new TreeSet<>();

    /**
     * Instantiates a new EntitySystemManager.
     *
     * @param context the context
     */
    public EntitySystemManager(final Context context) {
        super(context);

        statistics = context.getBean(Statistics.class);

        subscribe(this::handleUpdate, Update.class);
    }

    public EntitySystemManager add(EntitySystem entitySystem) {
        entitySystems.add(entitySystem);
        return this;
    }

    public SortedSet<EntitySystem> getAll() {
        return Collections.unmodifiableSortedSet(entitySystems);
    }

    public EntitySystemManager remove(EntitySystem entitySystem) {
        entitySystems.remove(entitySystem);
        return this;
    }

    public void removeAll() {
        entitySystems.clear();
    }

    private void handleUpdate(final Update update) {
        statistics.getCurrent().startSegment();
        entitySystems.stream()
                .filter(EntitySystem::isEnabled)
                .forEachOrdered(entitySystem -> {
                    try {
                        entitySystem.update(update);
                    } catch (ErisException e) {
                        LOG.error("Failed to update entity system {}", entitySystem.getClass().getSimpleName(), e);
                        return;
                    }
                });
        statistics.getCurrent().endSegment("Entity System Update");
    }
}
