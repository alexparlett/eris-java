package org.homonoia.eris.io.events;

import org.homonoia.eris.events.Event;
import org.homonoia.eris.io.Button;
import org.homonoia.eris.io.Modifier;

import java.util.List;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 05/03/2016
 */
public class MouseButtonUp extends Event {

    private final List<Modifier> mods;
    private final Button button;

    protected MouseButtonUp(final Builder builder) {
        super(builder);
        this.button = builder.button;
        this.mods = builder.mods;
    }

    public List<Modifier> getMods() {
        return mods;
    }

    public Button getButton() {
        return button;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public List<Modifier> mods;
        public Button button;

        public Builder mods(List<Modifier> mods) {
            this.mods = mods;
            return this;
        }

        public Builder button(Button button) {
            this.button = button;
            return this;
        }

        @Override
        public MouseButtonUp build() {
            return new MouseButtonUp(this);
        }
    }
}
