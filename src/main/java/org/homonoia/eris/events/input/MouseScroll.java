package org.homonoia.eris.events.input;

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
public class MouseScroll extends Event {

    private final Vector2d delta;
    private final Vector2d position;
    private final double timeStep;


    protected MouseScroll(final Builder builder) {
        super(builder);
        this.delta = builder.delta;
        this.position = builder.position;
        this.timeStep = builder.timeStep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public Vector2d delta;
        public Vector2d position;
        private double timeStep;

        public Builder delta(Vector2d delta) {
            this.delta = delta;
            return this;
        }

        public Builder position(Vector2d position) {
            this.position = position;
            return this;
        }

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }


        @Override
        public MouseScroll build() {
            return new MouseScroll(this);
        }
    }
}
