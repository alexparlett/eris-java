package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.EntityAdded;
import org.homonoia.eris.events.EntityRemoved;
import org.homonoia.eris.scripting.ScriptBinding;
import org.homonoia.eris.scripting.ScriptEngine;

import java.util.*;

/**
 * Created by alexparlett on 30/05/2016.
 */
public class EntityManager extends Contextual implements ScriptBinding {

    private Map<Long, Entity> idToEntity = new TreeMap<>();

    /**
     * Instantiates a new EntityManager.
     *
     * @param context the context
     */
    public EntityManager(final Context context) {
        super(context);
    }

    public EntityManager add(final Entity entity) {
        idToEntity.put(entity.getId(), entity);

        publish(EntityAdded.builder().entity(entity));

        return this;
    }

    public Optional<Entity> get(final Long id) {
        return Optional.ofNullable(idToEntity.get(id));
    }

    public Map<Long, Entity> getAll() {
        return Collections.unmodifiableMap(idToEntity);
    }

    public Optional<Entity> remove(final Long id) {
        Entity entity = idToEntity.remove(id);

        publish(EntityRemoved.builder().entity(entity));

        return Optional.ofNullable(entity);
    }

    public void removeAll() {
        Iterator<Map.Entry<Long, Entity>> iterator = idToEntity.entrySet().iterator();
        while(iterator.hasNext()) {
            Entity entity = iterator.next().getValue();

            publish(EntityRemoved.builder().entity(entity));

            iterator.remove();
        }
    }

    @Override
    public void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindGlobal("entityManager", this);
    }
}
