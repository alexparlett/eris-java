package org.homonoia.eris.events.frame;

import org.homonoia.eris.events.Event;

/**
 * Created by alexparlett on 12/12/2015.
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
