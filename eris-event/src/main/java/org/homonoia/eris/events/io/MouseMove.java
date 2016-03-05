package org.homonoia.eris.events.io;

import org.homonoia.eris.events.Event;
import org.joml.Vector2d;

/**
 * Created by alexp on 05/03/2016.
 */
public class MouseMove extends Event {

    private final Vector2d position;
    private final Vector2d delta;

    protected MouseMove(final Builder builder) {
        super(builder);
        this.position = builder.position;
        this.delta = builder.delta;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        public Vector2d position;
        public Vector2d delta;

        public Builder position(Vector2d position) {
            this.position = position;
            return this;
        }

        public Builder delta(Vector2d delta) {
            this.delta = delta;
            return this;
        }

        @Override
        public MouseMove build() {
            return new MouseMove(this);
        }
    }
}
