package org.homonoia.eris.events.ecs;

import org.homonoia.eris.ecs.Entity;
import org.homonoia.eris.events.Event;

/**
 * Created by alexparlett on 26/05/2016.
 */
public class EntityAdded extends Event {

    private Entity entity;

    private EntityAdded(Builder builder) {
        super(builder);
        this.entity = builder.entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private Entity entity;

        private Builder() {
        }

        public Builder entity(final Entity entity) {
            this.entity = entity;
            return this;
        }

        @Override
        public EntityAdded build() {
            return new EntityAdded(this);
        }
    }
}
