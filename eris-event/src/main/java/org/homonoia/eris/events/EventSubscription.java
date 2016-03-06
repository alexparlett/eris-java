package org.homonoia.eris.events;

/**
 * Created by alexparlett on 13/02/2016.
 */
public class EventSubscription {

    private final Class<?> eventClass;
    private final Object eventSource;

    private EventSubscription(Builder builder) {
        this.eventClass = builder.eventClass;
        this.eventSource = builder.eventSource;
    }

    /**
     * A method to check the equality of the Event Subscription event class without construction.
     *
     * @param eventClass  the event class
     * @return Whether the Event Class matches the one contained in the Event Subscription.
     */
    public boolean matches(final Class<?> eventClass) {
        return !(this.eventClass != null ? !this.eventClass.equals(eventClass) : eventClass != null);
    }

    /**
     * A method to check the equality of the Event Subscription event source without construction.
     *
     * @param eventSource  the event source
     * @return Whether the Event Class matches the one contained in the Event Subscription.
     */
    public boolean matches(final Object eventSource) {
        return !(this.eventSource != null ? !this.eventSource.equals(eventSource) : eventSource != null);
    }

    /**
     * A method to check the equality of the Event Subscription tuple without construction
     *
     * @param eventClass  the event class (optional)
     * @param eventSource the event source (optional)
     * @return Whether the Event Class and Event Source match those contained in the Event Subscription.
     */
    public boolean matches(final Class<?> eventClass, Object eventSource) {
        if (this.eventClass != null ? !this.eventClass.equals(eventClass) : eventClass != null) return false;
        return !(this.eventSource != null ? !this.eventSource.equals(eventSource) : eventSource != null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventSubscription that = (EventSubscription) o;

        if (eventClass != null ? !eventClass.equals(that.eventClass) : that.eventClass != null) return false;
        return !(eventSource != null ? !eventSource.equals(that.eventSource) : that.eventSource != null);

    }

    @Override
    public int hashCode() {
        int result = eventClass != null ? eventClass.hashCode() : 0;
        result = 31 * result + (eventSource != null ? eventSource.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Class<?> eventClass;
        private Object eventSource;

        private Builder() {
        }

        public EventSubscription build() {
            return new EventSubscription(this);
        }

        public Builder eventClass(Class<?> eventClass) {
            this.eventClass = eventClass;
            return this;
        }

        public Builder eventSource(Object eventSource) {
            this.eventSource = eventSource;
            return this;
        }
    }
}
