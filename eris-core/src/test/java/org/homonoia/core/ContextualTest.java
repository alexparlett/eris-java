package org.homonoia.core;

import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.frame.Begin;
import org.homonoia.eris.events.frame.End;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import rx.functions.Action1;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Contextual Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Feb 13, 2016</pre>
 */
@RunWith(MockitoJUnitRunner.class)
public class ContextualTest {

    @Mock
    Action1<End> EndHandlerMock;

    @Mock
    Action1<Begin> BeginFrameHandlerMock;

    private final Context context = new Context(new CommandLineArgs());

    /**
     * Method: subscribe(final Action1<T> eventAction)
     */
    @Test
    public void testSubscribeEventAction() throws Exception {
        Contextual publisher = new Contextual(context) {
        };

        Contextual subscriber = new Contextual(context) {
        };

        subscriber.subscribe(EndHandlerMock);
        publisher.publish(End.builder());

        verify(EndHandlerMock, times(1)).call(any());
    }

    /**
     * Method: subscribe(final Action1<T> eventAction, final Class<T> eventClass)
     */
    @Test
    public void testSubscribeForEventActionEventClass() throws Exception {
        Contextual publisher = new Contextual(context) {
        };

        Contextual subscriber = new Contextual(context) {
        };

        subscriber.subscribe(EndHandlerMock, End.class);
        publisher.publish(End.builder());
        publisher.publish(Begin.builder());

        verify(EndHandlerMock, times(1)).call(any());
    }

    /**
     * Method: subscribe(final Action1<T> eventAction, final Class<T> eventClass, final Object eventSrc)
     */
    @Test
    public void testSubscribeForEventActionEventClassEventSrc() throws Exception {
        Contextual publisher1 = new Contextual(context) {
        };

        Contextual publisher2 = new Contextual(context) {
        };

        Contextual subscriber = new Contextual(context) {
        };

        subscriber.subscribe(EndHandlerMock, End.class, publisher1);
        publisher1.publish(End.builder());
        publisher2.publish(End.builder());
        publisher1.publish(Begin.builder());
        publisher2.publish(Begin.builder());

        verify(EndHandlerMock, times(1)).call(any());
    }

    /**
     * Method: unsubscribe()
     */
    @Test
    public void testUnsubscribe() throws Exception {
        Contextual publisher = new Contextual(context) {
        };

        Contextual subscriber = new Contextual(context) {
        };

        subscriber.subscribe(EndHandlerMock, End.class);
        subscriber.subscribe(BeginFrameHandlerMock, Begin.class);

        subscriber.unsubscribe();

        publisher.publish(End.builder());
        publisher.publish(Begin.builder());

        verify(EndHandlerMock, never()).call(any());
        verify(BeginFrameHandlerMock, never()).call(any());

    }

    /**
     * Method: unsubscribe(final Class<?> eventClass)
     */
    @Test
    public void testUnsubscribeEventClass() throws Exception {
        Contextual publisher = new Contextual(context) {
        };

        Contextual subscriber = new Contextual(context) {
        };

        subscriber.subscribe(EndHandlerMock, End.class);
        subscriber.subscribe(BeginFrameHandlerMock, Begin.class);

        subscriber.unsubscribe(End.class);

        publisher.publish(End.builder());
        publisher.publish(Begin.builder());

        verify(EndHandlerMock, never()).call(any());
        verify(BeginFrameHandlerMock, times(1)).call(any());
    }

    /**
     * Method: unsubscribe(final Object eventSource)
     */
    @Test
    public void testUnsubscribeEventSource() throws Exception {
        Contextual publisher1 = new Contextual(context) {
        };

        Contextual publisher2 = new Contextual(context) {
        };

        Contextual subscriber = new Contextual(context) {
        };

        subscriber.subscribe(EndHandlerMock, End.class, publisher1);
        subscriber.subscribe(EndHandlerMock, End.class, publisher2);
        subscriber.subscribe(BeginFrameHandlerMock, Begin.class, publisher1);

        subscriber.unsubscribe(publisher1);

        publisher1.publish(End.builder());
        publisher2.publish(End.builder());
        publisher1.publish(Begin.builder());

        verify(EndHandlerMock, times(1)).call(any());
        verify(BeginFrameHandlerMock, never()).call(any());
    }

    /**
     * Method: unsubscribe(final Class<?> eventClass, final Object eventSrc)
     */
    @Test
    public void testUnsubscribeForEventClassEventSrc() throws Exception {
        Contextual publisher1 = new Contextual(context) {
        };

        Contextual publisher2 = new Contextual(context) {
        };

        Contextual subscriber = new Contextual(context) {
        };

        subscriber.subscribe(EndHandlerMock, End.class, publisher1);
        subscriber.subscribe(EndHandlerMock, End.class, publisher2);
        subscriber.subscribe(BeginFrameHandlerMock, Begin.class, publisher1);

        subscriber.unsubscribe(End.class, publisher1);

        publisher1.publish(End.builder());
        publisher2.publish(End.builder());
        publisher1.publish(Begin.builder());

        verify(EndHandlerMock, times(1)).call(any());
        verify(BeginFrameHandlerMock, times(1)).call(any());
    }
} 
