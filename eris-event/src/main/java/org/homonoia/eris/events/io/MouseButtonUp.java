package org.homonoia.eris.events.io;

import org.homonoia.eris.events.Event;

/**
 * Created by alexp on 05/03/2016.
 */
public class MouseButtonUp extends Event {

    private final int mods;
    private final int button;

    protected MouseButtonUp(final Builder builder) {
        super(builder);
        this.button = builder.button;
        this.mods = builder.mods;
    }

    public int getMods() {
        return mods;
    }

    public int getButton() {
        return button;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public int mods;
        public int button;

        public Builder mods(int mods) {
            this.mods = mods;
            return this;
        }

        public Builder button(int button) {
            this.button = button;
            return this;
        }

        @Override
        public MouseButtonUp build() {
            return new MouseButtonUp(this);
        }
    }
}
