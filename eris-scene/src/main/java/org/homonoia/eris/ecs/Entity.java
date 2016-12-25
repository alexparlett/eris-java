package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.ecs.annotations.Multiple;
import org.homonoia.eris.ecs.annotations.Requires;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.homonoia.eris.events.ComponentAdded;
import org.homonoia.eris.events.ComponentRemoved;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    public Entity add(final Component component) throws MissingRequiredComponentException {
        Objects.requireNonNull(component);

        boolean multiple = component.getClass().isAnnotationPresent(Multiple.class);

        if (component instanceof ScriptComponent) {
            ScriptComponent scriptComponent = (ScriptComponent) component;
            Class<? extends Component>[] classes = scriptComponent.requires();
            boolean autoAdd = scriptComponent.autoAdd();
            parseRequiredClass(classes, autoAdd, component);

            multiple = scriptComponent.multiple();
        } else if (component.getClass().isAnnotationPresent(Requires.class)) {
            Requires requires = component.getClass().getAnnotation(Requires.class);
            Class<? extends Component>[] classes = requires.classes();
            boolean autoAdd = requires.autoAdd();
            parseRequiredClass(classes, autoAdd, component);
        }

        if (!multiple) {
            remove(component.getClass());
        }

        components.add(component);
        publish(ComponentAdded.builder().component(component));

        return this;
    }

    public <T extends Component> Optional<T> get(final Class<T> clazz) {
        return components.stream()
                .filter(component -> component.getClass().equals(clazz))
                .map(component -> (T) component)
                .findFirst();
    }

    public <T extends Component> List<T> getAll(final Class<T> clazz) {
        return components.stream()
                .filter(component -> component.getClass().equals(clazz))
                .map(component -> (T) component)
                .collect(Collectors.toList());
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
        return components.stream()
                .filter(component -> component.getClass().equals(clazz))
                .findFirst()
                .isPresent();
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

    private void parseRequiredClass(Class<? extends Component>[] requires, boolean autoAdd, Component component) throws MissingRequiredComponentException {
        for(Class<? extends Component> require : requires) {
            boolean has = has(require);
            if (!has && !autoAdd) {
                throw new MissingRequiredComponentException(require, this, component);
            } else if (!has) {
                try {
                    add(require.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("Cannot add " + require.getName() + " automatically to entity when required as it does not have a valid no args constructor");
                }
            }
        }
    }
}
