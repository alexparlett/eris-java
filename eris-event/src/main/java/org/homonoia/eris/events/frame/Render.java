package org.homonoia.eris.events.frame;

import org.homonoia.eris.events.Event;

/**
 * Created by alexparlett on 26/05/2016.
 */
public class Render extends Event {

    private final double timeStep;

    private Render(Builder builder) {
        super(builder);
        this.timeStep = builder.timeStep;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {
        private double timeStep;

        private Builder() {
        }

        @Override
        public Render build() {
            return new Render(this);
        }

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }
    }
}
