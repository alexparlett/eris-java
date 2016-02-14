package org.homonoia.eris.events.graphics;

import org.homonoia.eris.events.Event;

/**
 * Created by alexparlett on 12/12/2015.
 */
public class Render extends Event {

    private Render(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private Builder() {
        }

        @Override
        public Render build() {
            return new Render(this);
        }
    }
}
