package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.frame.Update;

/**
 * Created by alexparlett on 30/05/2016.
 */
public abstract class EntitySystem extends Contextual implements Comparable<EntitySystem> {

    public static final int MAX_PRIORITY = 0;
    public static final int MIN_PRIORITY = Integer.MAX_VALUE;

    protected FamilyManager familyManager;

    private boolean enabled;
    private int priority;

    /**
     * Instantiates a new EntitySystem.
     *
     * @param context the context
     */
    public EntitySystem(final Context context) {
        this(context, MAX_PRIORITY);
        this.familyManager = context.getBean(FamilyManager.class);
    }

    /**
     * Instantiates a new EntitySystem.
     *
     * @param context the context
     */
    public EntitySystem(final Context context, final int priority) {
        super(context);
        this.priority = priority;
        this.enabled = true;
    }

    public abstract void update(final Update update);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntitySystem that = (EntitySystem) o;

        if (enabled != that.enabled) return false;
        return priority == that.priority;

    }

    @Override
    public int hashCode() {
        int result = (enabled ? 1 : 0);
        result = 31 * result + priority;
        return result;
    }

    @Override
    public int compareTo(final EntitySystem o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
