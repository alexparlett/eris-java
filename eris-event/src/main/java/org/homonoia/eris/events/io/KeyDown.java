package org.homonoia.eris.events.io;

import org.homonoia.eris.events.Event;

/**
 * Created by alexp on 05/03/2016.
 */
public class KeyDown extends Event {

    private final boolean repeat;
    private final int key;
    private final int scancode;
    private final int mods;
    private final String character;

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

    public int getKey() {
        return key;
    }

    public int getScancode() {
        return scancode;
    }

    public int getMods() {
        return mods;
    }

    public String getCharacter() {
        return character;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private boolean repeat;
        private int mods;
        private int key;
        private int scancode;
        private String character;

        public Builder key(final int key) {
            this.key = key;
            return this;
        }

        public Builder scancode(final int scancode) {
            this.scancode = scancode;
            return this;
        }

        public Builder mods(final int mods) {
            this.mods = mods;
            return this;
        }

        public Builder repeat(final boolean repeat) {
            this.repeat = repeat;
            return this;
        }

        public Builder character(final String character) {
            this.character = character;
            return this;
        }

        @Override
        public KeyDown build() {
            return new KeyDown(this);
        }
    }
}
