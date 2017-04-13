package org.homonoia.eris.core;

import lombok.extern.slf4j.Slf4j;
import org.homonoia.eris.events.Event;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * The type Contextual.
 */
@Slf4j
public class Context {

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
    private final Map<Class, Object> beans = new HashMap<>();
    private final CommandLineArgs commandLineArgs;

    public Context(CommandLineArgs commandLineArgs) {
        this.commandLineArgs = commandLineArgs;
        this.debugEnabled.set(commandLineArgs.containsOption("debug"));
    }

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

    public <T> T getBean(Class<T> clazz) {
        Object found = beans.get(clazz);
        if (nonNull(found)) {
            return (T) found;
        }

        return beans.entrySet().stream()
                .filter(classObjectEntry -> clazz.isAssignableFrom(classObjectEntry.getKey()))
                .findFirst()
                .map(classObjectEntry -> (T) classObjectEntry.getValue())
                .orElse(null);
    }

    public <T> List<T> getBeans(Class<T> clazz) {
        return beans.entrySet().stream()
                .filter(classObjectEntry -> clazz.isAssignableFrom(classObjectEntry.getKey()))
                .map(classObjectEntry -> (T) classObjectEntry.getValue())
                .collect(Collectors.toList());
    }

    public <T> T registerBean(T bean) {
        log.info("Registering Bean {}", bean.getClass().getCanonicalName());
        beans.put(bean.getClass(), bean);
        return bean;
    }

    public CommandLineArgs getCommandLineArgs() {
        return commandLineArgs;
    }
}