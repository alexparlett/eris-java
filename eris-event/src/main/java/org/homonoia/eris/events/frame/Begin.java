package org.homonoia.eris.events.frame;

import org.homonoia.eris.events.Event;

/**
 * Created by alexparlett on 26/05/2016.
 */
public class Begin extends Event {

    private final double timeStep;

    private Begin(Builder builder) {
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
        public Begin build() {
            return new Begin(this);
        }

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }
    }
}
