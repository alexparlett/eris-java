package org.homonoia.eris.events;

import org.homonoia.eris.ecs.Component;

/**
 * Created by alexparlett on 26/05/2016.
 */
public class ComponentAdded extends Event {

    private Component component;

    private ComponentAdded(Builder builder) {
        super(builder);
        this.component = builder.component;
    }

    public Component getComponent() {
        return component;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private Component component;

        private Builder() {
        }

        public Builder component(final Component component) {
            this.component = component;
            return this;
        }

        @Override
        public ComponentAdded build() {
            return new ComponentAdded(this);
        }
    }
}
