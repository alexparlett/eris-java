package org.homonoia.eris.input.events;

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
public class KeyDown extends Event {

    private final boolean repeat;
    private final Key key;
    private final int scancode;
    private final char character;
    private final List<Modifier> mods;

    protected KeyDown(final Builder builder) {
        super(builder);
        this.repeat = builder.repeat;
        this.key = builder.key;
        this.scancode = builder.scancode;
        this.mods = builder.mods;
        this.character = builder.character;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public Key getKey() {
        return key;
    }

    public int getScancode() {
        return scancode;
    }

    public List<Modifier> getMods() {
        return mods;
    }

    public char getCharacter() {
        return character;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private boolean repeat;
        private List<Modifier> mods;
        private Key key;
        private int scancode;
        private char character;

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

        public Builder repeat(final boolean repeat) {
            this.repeat = repeat;
            return this;
        }

        @Override
        public KeyDown build() {
            return new KeyDown(this);
        }
    }
}