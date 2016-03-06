package org.homonoia.eris.events.core;

import org.homonoia.eris.events.Event;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 12/12/2015
 */
public class ExitRequested extends Event {

    private ExitRequested(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private Builder() {
        }

        @Override
        public ExitRequested build() {
            return new ExitRequested(this);
        }
    }
}
