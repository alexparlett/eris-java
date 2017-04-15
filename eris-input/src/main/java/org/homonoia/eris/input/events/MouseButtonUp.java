package org.homonoia.eris.input.events;

import lombok.Getter;
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
@Getter
public class MouseButtonUp extends Event {

    private final List<Modifier> mods;
    private final Button button;
    private final Vector2d position;
    private final double timeStep;

    protected MouseButtonUp(final Builder builder) {
        super(builder);
        this.button = builder.button;
        this.mods = builder.mods;
        this.position = builder.position;
        this.timeStep = builder.timeStep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public List<Modifier> mods;
        public Button button;
        public Vector2d position;
        private double timeStep;

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

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }


        @Override
        public MouseButtonUp build() {
            return new MouseButtonUp(this);
        }
    }
}
