package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.frame.Update;

/**
 * Created by alexparlett on 30/05/2016.
 */
public abstract class EntitySystem extends Contextual implements Comparable<EntitySystem> {

    private boolean enable;
    private int priority;

    /**
     * Instantiates a new EntitySystem.
     *
     * @param context the context
     */
    public EntitySystem(final Context context) {
        this(context, 0);
    }

    /**
     * Instantiates a new EntitySystem.
     *
     * @param context the context
     */
    public EntitySystem(final Context context, final int priority) {
        super(context);
        this.priority = priority;
        this.enable = true;
    }

    public abstract void update(final Update update);

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(final boolean enable) {
        this.enable = enable;
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

        if (enable != that.enable) return false;
        return priority == that.priority;

    }

    @Override
    public int hashCode() {
        int result = (enable ? 1 : 0);
        result = 31 * result + priority;
        return result;
    }

    @Override
    public int compareTo(final EntitySystem o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
