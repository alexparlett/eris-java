package org.homonoia.eris.core;

import org.homonoia.eris.events.Event;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The type Contextual.
 */
public class Context implements ApplicationContextAware {

    private final AtomicReference<ExitCode> exitCode = new AtomicReference<>(ExitCode.SUCCESS);
    private final Subject<Event, Event> subject = new SerializedSubject<>(PublishSubject.create());
    private ApplicationContext applicationContext;

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

    public ExitCode getExitCode() {
        return exitCode.get();
    }

    public void setExitCode(final ExitCode exitCode) {
        this.exitCode.set(exitCode);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public <T extends Contextual> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}