package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.frame.Update;

/**
 * Created by alexparlett on 26/05/2016.
 */
public class EntityComponentSystem extends Contextual {

    private final EntitySystemManager entitySystemManager;
    private final EntityManager entityManager;

    /**
     * Instantiates a new EntityComponentSystem.
     *  @param context the context
     * @param entitySystemManager
     * @param entityManager
     */
    public EntityComponentSystem(final Context context, final EntitySystemManager entitySystemManager, final EntityManager entityManager) {
        super(context);
        this.entitySystemManager = entitySystemManager;
        this.entityManager = entityManager;

        subscribe(this::handleUpdate, Update.class);
    }

    private void handleUpdate(final Update update) {

    }
}
