package org.homonoia.eris.events.frame;

import org.homonoia.eris.events.Event;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 12/12/2015
 */
public class BeginFrame extends Event {

    private final int frameNumber;
    private final double timeStep;

    private BeginFrame(Builder builder) {
        super(builder);
        this.frameNumber = builder.frameNumber;
        this.timeStep = builder.timeStep;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {
        private int frameNumber;
        private double timeStep;

        private Builder() {
        }

        @Override
        public BeginFrame build() {
            return new BeginFrame(this);
        }

        public Builder frameNumber(int frameNumber) {
            this.frameNumber = frameNumber;
            return this;
        }

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }
    }
}
