package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by alexparlett on 30/05/2016.
 */
public class EntitySystemManager extends Contextual {

    private SortedSet<EntitySystem> entitySystems = new TreeSet<>();

    /**
     * Instantiates a new EntitySystemManager.
     *
     * @param context the context
     */
    public EntitySystemManager(final Context context) {
        super(context);
    }
}
