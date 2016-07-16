package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.ComponentAdded;
import org.homonoia.eris.events.ComponentRemoved;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2016
 */
public class Family extends Contextual {

    private Set<Entity> entities = new ConcurrentSkipListSet<>();
    private Set<Class<? extends Component>> watchedComponents;

    public Family(Context context, Set<Class<? extends Component>> watchedComponents) {
        super(context);
        for (Class<? extends Component> watchComponent : watchedComponents) {
            subscribe(this::handleComponentAdded, ComponentAdded.class, null, getComponentAddedPredicate(watchComponent));
            subscribe(this::handleComponentRemoved, ComponentRemoved.class, null, getComponentRemovedPredicate(watchComponent));
        }
        this.watchedComponents = watchedComponents;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public Set<Class<? extends Component>> getWatchedComponents() {
        return watchedComponents;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Family family = (Family) o;

        if (entities != null ? !entities.equals(family.entities) : family.entities != null) return false;
        return watchedComponents != null ? watchedComponents.equals(family.watchedComponents) : family.watchedComponents == null;

    }

    @Override
    public int hashCode() {
        int result = entities != null ? entities.hashCode() : 0;
        result = 31 * result + (watchedComponents != null ? watchedComponents.hashCode() : 0);
        return result;
    }

    private Predicate<ComponentAdded> getComponentAddedPredicate(final Class<? extends Component> watchClass) {
        return componentAdded -> Objects.equals(componentAdded.getComponent().getClass(), watchClass);
    }

    private Predicate<ComponentRemoved> getComponentRemovedPredicate(final Class<? extends Component> watchClass) {
        return componentRemoved -> Objects.equals(componentRemoved.getComponent().getClass(), watchClass);
    }

    private void handleComponentAdded(final ComponentAdded componentAdded) {
        entities.add((Entity) componentAdded.getSource());
    }

    private void handleComponentRemoved(final ComponentRemoved componentRemoved) {
        entities.remove(componentRemoved.getSource());
    }
}
