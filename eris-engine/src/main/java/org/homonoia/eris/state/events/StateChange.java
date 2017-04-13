package org.homonoia.eris.state.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.homonoia.eris.events.Event;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 12/12/2015
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StateChange extends Event {

    private Long id;

    private StateChange(Builder builder) {
        super(builder);
        this.id = builder.id;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Getter
    public static final class Builder extends EventBuilder<Builder> {

        private Long id;

        private Builder() {
        }

        public Builder id(@NonNull Long id) {
            this.id = id;
            return this;
        }

        @Override
        public StateChange build() {
            return new StateChange(this);
        }
    }
}
