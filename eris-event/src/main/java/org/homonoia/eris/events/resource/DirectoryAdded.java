package org.homonoia.eris.events.resource;

import lombok.Getter;
import org.homonoia.eris.events.Event;

import java.nio.file.Path;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 12/12/2015
 */
@Getter
public class DirectoryAdded extends Event {

    private Path path;
    private int priority;

    private DirectoryAdded(Builder builder) {
        super(builder);
        this.path = builder.path;
        this.priority = builder.priority;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends EventBuilder<Builder> {

        private Path path;
        private int priority;

        private Builder() {
        }

        public Builder path(final Path path) {
            this.path = path;
            return this;
        }

        public Builder priority(final int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public DirectoryAdded build() {
            return new DirectoryAdded(this);
        }
    }
}
