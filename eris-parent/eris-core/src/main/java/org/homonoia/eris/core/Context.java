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
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * The type Contextual.
 */
public class Context implements ApplicationContextAware {

    public final static <T extends Event> Predicate<T> eventClassPredicate(Class eventClass) {
        return event -> Optional.ofNullable(eventClass)
                .map(ec -> Objects.equals(ec, event.getClass()))
                .orElse(true);
    }

    public final static <T extends Event> Predicate<T> eventSourcePredicate(Object eventSource) {
        return event -> Optional.ofNullable(eventSource)
                .map(es -> Objects.equals(es, event.getSource()))
                .orElse(true);
    }

    private final AtomicBoolean debugEnabled = new AtomicBoolean(false);
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
     * Register for an event filtering by class, source and a custom predicate.
     *
     * @param <T>         the type parameter
     * @param eventAction the event action
     * @param filter      the event filter
     * @return the subscription
     */
    public <T extends Event> Subscription subscribe(final Action1<T> eventAction, final Predicate<T> filter) {
        return subject.map(obj -> (T) obj)
                .filter(event -> !event.isStopPropagation())
                .filter(filter::test)
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

    public boolean isDebugEnabled() {
        return debugEnabled.get();
    }

    public void setDebugEnabled(final Boolean debugEnabled) {
        this.debugEnabled.set(debugEnabled);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public <T extends Annotation> Map<String, Object> getBeansWithAnnotation(Class<T> clazz) {
        return applicationContext.getBeansWithAnnotation(clazz);
    }

}