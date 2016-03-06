package org.homonoia.eris.input.events;

import org.homonoia.eris.events.Event;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 05/03/2016
 */
public class MouseScroll extends Event {

    private final double delta;

    protected MouseScroll(final Builder builder) {
        super(builder);
        this.delta = builder.delta;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public double delta;

        public Builder delta(double delta) {
            this.delta = delta;
            return this;
        }

        @Override
        public MouseScroll build() {
            return new MouseScroll(this);
        }
    }
}
