package org.homonoia.eris.events.frame;

import org.homonoia.eris.events.Event;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 12/12/2015
 */
public class EndFrame extends Event {

    private EndFrame(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private Builder() {
        }

        @Override
        public EndFrame build() {
            return new EndFrame(this);
        }
    }
}
