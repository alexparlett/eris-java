package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.ComponentRemoved;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by alexparlett on 26/05/2016.
 */
public abstract class Entity extends Contextual {

    private Set<? extends Component> components = new HashSet<>();

    /**
     * Instantiates a new Entity.
     *
     * @param context the context
     */
    public Entity(final Context context) {
        super(context);
    }

    public <T extends Component> Entity add(final T component) {
        return this;
    }

    public <T extends Component> T get(final Class<T> component) {
        return null;
    }

    public Set<? extends Component> getAll() {
        return Collections.unmodifiableSet(components);
    }

    public <T extends Component> T remove(final Class<T> component) {
        return null;
    }

    public void removeAll() {
        Iterator<? extends Component> iterator = components.iterator();
        while(iterator.hasNext()) {
            Component component = iterator.next();

            publishComponentRemoved(component);

            iterator.remove();
        }
    }

    public boolean has(final Class<? extends Component> component) {
        return false;
    }

    public void publishComponentRemoved(final Component component) {
        publish(ComponentRemoved.builder()
                .component(component));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return components != null ? components.equals(entity.components) : entity.components == null;

    }

    @Override
    public int hashCode() {
        return components != null ? components.hashCode() : 0;
    }
}
