package org.homonoia.eris.input.events;

import org.homonoia.eris.events.Event;
import org.joml.Vector2d;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 05/03/2016
 */
public class MouseScroll extends Event {

    private final double delta;
    private final Vector2d position;

    protected MouseScroll(final Builder builder) {
        super(builder);
        this.delta = builder.delta;
        this.position = builder.position;
    }

    public double getDelta() {
        return delta;
    }

    public Vector2d getPosition() {
        return position;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public double delta;
        public Vector2d position;

        public Builder delta(double delta) {
            this.delta = delta;
            return this;
        }

        public Builder position(Vector2d position) {
            this.position = position;
            return this;
        }

        @Override
        public MouseScroll build() {
            return new MouseScroll(this);
        }
    }
}
