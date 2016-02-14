package org.homonoia.eris.core;

import org.homonoia.eris.events.Event;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import javax.annotation.PreDestroy;
import java.util.Optional;

/**
 * The type Contextual.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Context {

    private final Subject<Event, Event> subject = new SerializedSubject<>(PublishSubject.create());

    /**
     * Ensures that all subscriptions to the context are properly unsubscribed when it is deleted.
     */
    @PreDestroy
    public void destroy() {
        subject.onCompleted();
    }

    /**
     * Register for an event filtering by class and source.
     *
     * @param <T>         the type parameter
     * @param eventClass  the event class
     * @param eventAction the event action
     * @param eventSrc    the event source
     * @return the subscription
     */
    public <T extends Event> Subscription subscribe(final Action1<T> eventAction, final Class<T> eventClass, final Object eventSrc) {
        return subject
                .filter(event -> Optional.ofNullable(eventClass)
                        .map(ec -> ec.equals(event.getClass()))
                        .orElse(true))
                .filter(event -> Optional.ofNullable(eventSrc)
                        .map(es -> es.equals(event.getSource()))
                        .orElse(true))
                .map(obj -> (T) obj)
                .subscribe(eventAction);
    }

    /**
     * Publish.
     *
     * @param event the event
     */
    public void publish(Event event) {
        subject.onNext(event);
    }

}