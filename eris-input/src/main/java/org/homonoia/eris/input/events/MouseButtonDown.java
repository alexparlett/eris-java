package org.homonoia.eris.input.events;

import org.homonoia.eris.events.Event;
import org.homonoia.eris.input.Button;
import org.homonoia.eris.input.Modifier;
import org.joml.Vector2d;

import java.util.List;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 05/03/2016
 */
public class MouseButtonDown extends Event {

    private final List<Modifier> mods;
    private final Button button;
    private final Vector2d position;

    protected MouseButtonDown(final Builder builder) {
        super(builder);
        this.button = builder.button;
        this.mods = builder.mods;
        this.position = builder.position;
    }

    public List<Modifier> getMods() {
        return mods;
    }

    public Button getButton() {
        return button;
    }

    public Vector2d getPosition() {
        return position;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public List<Modifier> mods;
        public Button button;
        public Vector2d position;

        public Builder mods(List<Modifier> mods) {
            this.mods = mods;
            return this;
        }

        public Builder button(Button button) {
            this.button = button;
            return this;
        }

        public Builder position(Vector2d position) {
            this.position = position;
            return this;
        }

        @Override
        public MouseButtonDown build() {
            return new MouseButtonDown(this);
        }
    }
}
