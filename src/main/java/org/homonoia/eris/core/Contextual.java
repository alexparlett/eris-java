package org.homonoia.eris.core;

import org.homonoia.eris.events.Event;
import org.homonoia.eris.events.EventSubscription;
import rx.Subscription;
import rx.functions.Action1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 13/02/2016
 */
public abstract class Contextual {

    private final Context context;
    private final Map<EventSubscription, Subscription> subscriptions = new HashMap<>();

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public Contextual(final Context context) {
        this.context = context;
    }

    /**
     * Destroy method, ensures that all subscriptions are unsubscribed before deletion.
     */
    public void destroy() {
        unsubscribe();
    }

    /**
     * Subscribe to an event.
     *
     * @param <T>         the type parameter
     * @param eventAction the event action
     */
    public synchronized <T extends Event> void subscribe(final Action1<T> eventAction) {
        subscribe(eventAction, null, null);
    }

    /**
     * Subscribe to an event filtering by class.
     *
     * @param <T>         the type parameter
     * @param eventAction the event action
     * @param eventClass  the event class
     */
    public synchronized <T extends Event> void subscribe(final Action1<T> eventAction, final Class<T> eventClass) {
        subscribe(eventAction, eventClass, null);
    }

    /**
     * Subscribe to an event filtering by class and source.
     *
     * @param <T>         the type parameter
     * @param eventAction the event action
     * @param eventClass  the event class
     * @param eventSrc    the event src
     */
    public synchronized <T extends Event> void subscribe(final Action1<T> eventAction, final Class<T> eventClass, final Object eventSrc) {
        Predicate<T> predicate = (Predicate<T>) Context.eventSourcePredicate(eventSrc).and(Context.eventClassPredicate(eventClass));
        Subscription subscription = context.subscribe(eventAction, predicate);
        EventSubscription eventSubscription = EventSubscription.builder()
                .eventClass(eventClass)
                .eventSource(eventSrc)
                .build();
        subscriptions.put(eventSubscription, subscription);
    }

    /**
     * Subscribe to an event filtering by class and source.
     *
     * @param <T>         the type parameter
     * @param eventAction the event action
     * @param eventClass  the event class
     * @param eventSrc    the event src
     * @param filter      the filter
     */
    public synchronized <T extends Event> void subscribe(final Action1<T> eventAction, final Class<T> eventClass, final Object eventSrc, final Predicate<T> filter) {
        Predicate<T> predicate = (Predicate<T>) Context.eventSourcePredicate(eventSrc).and(Context.eventClassPredicate(eventClass));
        predicate = predicate.and(filter);

        Subscription subscription = context.subscribe(eventAction, predicate);
        EventSubscription eventSubscription = EventSubscription.builder()
                .eventClass(eventClass)
                .eventSource(eventSrc)
                .build();
        subscriptions.put(eventSubscription, subscription);
    }

    /**
     * Unsubscribe from all Events
     */
    public synchronized void unsubscribe() {
        subscriptions.forEach((eventSubscription, subscription) -> subscription.unsubscribe());
        subscriptions.clear();
    }

    /**
     * Unsubscribe from all Events of a certain {@link Class}
     *
     * @param eventClass the event class
     */
    public synchronized void unsubscribe(final Class<?> eventClass) {
        subscriptions.entrySet()
                .stream()
                .filter(entry -> entry.getKey().matches(eventClass))
                .map(Map.Entry::getValue)
                .forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }

    /**
     * Unsubscribe from all Events from a certain {@link Object Source}
     *
     * @param eventSource the event source
     */
    public synchronized void unsubscribe(final Object eventSource) {
        subscriptions.entrySet()
                .stream()
                .filter(entry -> entry.getKey().matches(eventSource))
                .map(Map.Entry::getValue)
                .forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }


    /**
     * Unsubscribe from all Events from a certain {@link Class} and {@link Object Source}
     *
     * @param eventClass the event class
     * @param eventSrc   the event src
     */
    public synchronized void unsubscribe(final Class<?> eventClass, final Object eventSrc) {
        subscriptions.entrySet()
                .stream()
                .filter(entry -> entry.getKey().matches(eventClass, eventSrc))
                .map(Map.Entry::getValue)
                .forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }

    /**
     * Publish an event to subscribers.
     *
     * @param eventBuilder the event builder for the event.
     */
    public void publish(final Event.EventBuilder<?> eventBuilder) {
        eventBuilder.source(this);
        context.publish(eventBuilder.build());
    }

    public Context getContext() {
        return context;
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }
}
