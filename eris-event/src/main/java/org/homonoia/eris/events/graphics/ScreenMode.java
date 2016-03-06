package org.homonoia.eris.events.graphics;

import org.homonoia.eris.events.Event;

/**
 * Created by alexparlett on 12/12/2015.
 */
public class ScreenMode extends Event {

    private final int width;
    private final int height;

    private ScreenMode(final Builder builder) {
        super(builder);
        this.width = builder.width;
        this.height = builder.height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private int width;
        private int height;

        private Builder() {
        }

        public Builder width(final int width) {
            this.width = width;
            return this;
        }

        public Builder height(final int height) {
            this.height = height;
            return this;
        }

        @Override
        public ScreenMode build() {
            return new ScreenMode(this);
        }
    }
}
