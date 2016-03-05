package org.homonoia.eris.io.events;

import org.homonoia.eris.events.Event;

/**
 * Created by alexp on 05/03/2016.
 */
public class Text extends Event {

    private final String string;

    protected Text(final Builder builder) {
        super(builder);
        this.string = builder.string;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public String string;

        public Builder string(String string) {
            this.string = string;
            return this;
        }

        @Override
        public Text build() {
            return new Text(this);
        }
    }
}
