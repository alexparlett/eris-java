package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.exceptions.ErisException;
import org.homonoia.eris.ecs.systems.InputSystem;
import org.homonoia.eris.ecs.systems.RenderSystem;
import org.homonoia.eris.events.frame.Update;
import org.homonoia.eris.scripting.ScriptBinding;
import org.homonoia.eris.scripting.ScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by alexparlett on 30/05/2016.
 */
public class EntitySystemManager extends Contextual implements ScriptBinding {

    private static final Logger LOG = LoggerFactory.getLogger(EntitySystemManager.class);

    private SortedSet<EntitySystem> entitySystems = new TreeSet<>();

    /**
     * Instantiates a new EntitySystemManager.
     *
     * @param context the context
     */
    public EntitySystemManager(final Context context) {
        super(context);
        subscribe(this::handleUpdate, Update.class);

        add(new RenderSystem(context));
        add(new InputSystem(context));
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

    @Override
    public void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindClass(EntitySystem.class);
        scriptEngine.bindGlobal("entitySystemManager", this);
    }

    private void handleUpdate(final Update update) {
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
    }
}
