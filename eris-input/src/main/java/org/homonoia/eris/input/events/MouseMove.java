package org.homonoia.eris.input.events;

import lombok.Getter;
import org.homonoia.eris.events.Event;
import org.joml.Vector2d;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 05/03/2016
 */
@Getter
public class MouseMove extends Event {

    private final Vector2d position;
    private final Vector2d delta;
    private final double timeStep;

    protected MouseMove(final Builder builder) {
        super(builder);
        this.position = builder.position;
        this.delta = builder.delta;
        this.timeStep = builder.timeStep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public Vector2d position;
        public Vector2d delta;
        private double timeStep;

        public Builder position(Vector2d position) {
            this.position = position;
            return this;
        }

        public Builder delta(Vector2d delta) {
            this.delta = delta;
            return this;
        }

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }

        @Override
        public MouseMove build() {
            return new MouseMove(this);
        }
    }
}
