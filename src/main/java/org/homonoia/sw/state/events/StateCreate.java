package org.homonoia.sw.state.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.homonoia.eris.events.Event;
import org.homonoia.sw.state.State;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 12/12/2015
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StateCreate extends Event {
    private State state;
    private Long id;

    private StateCreate(Builder builder) {
        super(builder);
        this.state = builder.state;
        this.id = builder.id;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Getter
    public static final class Builder extends EventBuilder<Builder> {

        private State state;
        private Long id;

        private Builder() {
        }

        public Builder state(@NonNull State state) {
            this.state = state;
            return this;
        }

        public Builder id(@NonNull Long id) {
            this.id = id;
            return this;
        }

        @Override
        public StateCreate build() {
            return new StateCreate(this);
        }
    }
}
