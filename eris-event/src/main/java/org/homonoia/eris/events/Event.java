package org.homonoia.eris.events;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 08/12/2015
 */
public abstract class Event {

    private final Object source;

    protected Event(EventBuilder<?> eventBuilder) {
        this.source = eventBuilder.source;
    }

    public Object getSource() {
        return source;
    }

    public static abstract class EventBuilder<T extends EventBuilder> {

        private Object source;

        protected EventBuilder() {
        }

        public T source(Object source) {
            this.source = source;
            return (T) this;
        }

        public abstract <K extends Event> K build();
    }
}
