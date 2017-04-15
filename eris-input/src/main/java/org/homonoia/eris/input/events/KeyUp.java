package org.homonoia.eris.input.events;

import lombok.Getter;
import org.homonoia.eris.events.Event;
import org.homonoia.eris.input.Key;
import org.homonoia.eris.input.Modifier;

import java.util.List;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 05/03/2016
 */
@Getter
public class KeyUp extends Event {

    private final Key key;
    private final int scancode;
    private final List<Modifier> mods;
    private final char character;
    private final double timeStep;

    protected KeyUp(final Builder builder) {
        super(builder);
        this.key = builder.key;
        this.scancode = builder.scancode;
        this.mods = builder.mods;
        this.character = builder.character;
        this.timeStep = builder.timeStep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private List<Modifier> mods;
        private Key key;
        private int scancode;
        private char character;
        private double timeStep;

        public Builder key(final Key key) {
            this.key = key;
            return this;
        }

        public Builder scancode(final int scancode) {
            this.scancode = scancode;
            return this;
        }

        public Builder character(final char character) {
            this.character = character;
            return this;
        }

        public Builder mods(final List<Modifier> mods) {
            this.mods = mods;
            return this;
        }

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }


        @Override
        public KeyUp build() {
            return new KeyUp(this);
        }
    }
}
