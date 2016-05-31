package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.ComponentAdded;
import org.homonoia.eris.events.ComponentRemoved;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by alexparlett on 26/05/2016.
 */
public final class Entity extends Contextual {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private final long id = ID_GENERATOR.incrementAndGet();
    private final Set<Component> components = new HashSet<>();

    /**
     * Instantiates a new Entity.
     *
     * @param context the context
     */
    public Entity(final Context context) {
        super(context);
    }

    public long getId() {
        return id;
    }

    public Entity add(final Component component) {
        Objects.requireNonNull(component);

        remove(component.getClass());

        components.add(component);

        publish(ComponentAdded.builder().component(component));

        return this;
    }

    public <T extends Component> Optional<T> get(final Class<T> clazz) {
        Iterator<? extends Component> iterator = components.iterator();
        while(iterator.hasNext()) {
            Component component = iterator.next();

            if (component.getClass().equals(clazz)) {
                return Optional.of((T) component);
            }
        }

        return Optional.empty();
    }

    public Set<? extends Component> getAll() {
        return Collections.unmodifiableSet(components);
    }

    public <T extends Component> Optional<T> remove(final Class<T> clazz) {
        Iterator<? extends Component> iterator = components.iterator();
        while(iterator.hasNext()) {
            Component component = iterator.next();

            if (component.getClass().equals(clazz)) {
                publishComponentRemoved(component);
                iterator.remove();
                return Optional.of((T) component);
            }
        }

        return Optional.empty();
    }

    public void removeAll() {
        Iterator<? extends Component> iterator = components.iterator();
        while(iterator.hasNext()) {
            Component component = iterator.next();

            publishComponentRemoved(component);

            iterator.remove();
        }
    }

    public boolean has(final Class<? extends Component> clazz) {
        Iterator<? extends Component> iterator = components.iterator();
        while(iterator.hasNext()) {
            Component component = iterator.next();

            if (component.getClass().equals(clazz)) {
                return true;
            }
        }

        return false;
    }

    protected void publishComponentRemoved(final Component component) {
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
