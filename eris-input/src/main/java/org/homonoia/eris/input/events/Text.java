package org.homonoia.eris.input.events;

import lombok.Getter;
import org.homonoia.eris.events.Event;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 05/03/2016
 */
@Getter
public class Text extends Event {

    private final String string;
    private final double timeStep;

    protected Text(final Builder builder) {
        super(builder);
        this.string = builder.string;
        this.timeStep = builder.timeStep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public String string;
        private double timeStep;

        public Builder string(String string) {
            this.string = string;
            return this;
        }

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }

        @Override
        public Text build() {
            return new Text(this);
        }
    }
}
