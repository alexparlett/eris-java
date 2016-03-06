package org.homonoia.core;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.frame.BeginFrame;
import org.homonoia.eris.events.frame.EndFrame;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.functions.Action1;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
    Action1<EndFrame> endFrameHandlerMock;

    @Mock
    Action1<BeginFrame> beginFrameFrameHandlerMock;

    private final Context context = new Context();

    /**
     * Method: subscribe(final Action1<T> eventAction)
     */
    @Test
    public void testSubscribeEventAction() throws Exception {
        Contextual publisher = new Contextual(context) {
        };

        Contextual subscriber = new Contextual(context) {
        };

        subscriber.subscribe(endFrameHandlerMock);
        publisher.publish(EndFrame.builder());

        verify(endFrameHandlerMock, times(1)).call(any());
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

        subscriber.subscribe(endFrameHandlerMock, EndFrame.class);
        publisher.publish(EndFrame.builder());
        publisher.publish(BeginFrame.builder());

        verify(endFrameHandlerMock, times(1)).call(any());
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

        subscriber.subscribe(endFrameHandlerMock, EndFrame.class, publisher1);
        publisher1.publish(EndFrame.builder());
        publisher2.publish(EndFrame.builder());
        publisher1.publish(BeginFrame.builder());
        publisher2.publish(BeginFrame.builder());

        verify(endFrameHandlerMock, times(1)).call(any());
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

        subscriber.subscribe(endFrameHandlerMock, EndFrame.class);
        subscriber.subscribe(beginFrameFrameHandlerMock, BeginFrame.class);

        subscriber.unsubscribe();

        publisher.publish(EndFrame.builder());
        publisher.publish(BeginFrame.builder());

        verify(endFrameHandlerMock, never()).call(any());
        verify(beginFrameFrameHandlerMock, never()).call(any());

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

        subscriber.subscribe(endFrameHandlerMock, EndFrame.class);
        subscriber.subscribe(beginFrameFrameHandlerMock, BeginFrame.class);

        subscriber.unsubscribe(EndFrame.class);

        publisher.publish(EndFrame.builder());
        publisher.publish(BeginFrame.builder());

        verify(endFrameHandlerMock, never()).call(any());
        verify(beginFrameFrameHandlerMock, times(1)).call(any());
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

        subscriber.subscribe(endFrameHandlerMock, EndFrame.class, publisher1);
        subscriber.subscribe(endFrameHandlerMock, EndFrame.class, publisher2);
        subscriber.subscribe(beginFrameFrameHandlerMock, BeginFrame.class, publisher1);

        subscriber.unsubscribe(publisher1);

        publisher1.publish(EndFrame.builder());
        publisher2.publish(EndFrame.builder());
        publisher1.publish(BeginFrame.builder());

        verify(endFrameHandlerMock, times(1)).call(any());
        verify(beginFrameFrameHandlerMock, never()).call(any());
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

        subscriber.subscribe(endFrameHandlerMock, EndFrame.class, publisher1);
        subscriber.subscribe(endFrameHandlerMock, EndFrame.class, publisher2);
        subscriber.subscribe(beginFrameFrameHandlerMock, BeginFrame.class, publisher1);

        subscriber.unsubscribe(EndFrame.class, publisher1);

        publisher1.publish(EndFrame.builder());
        publisher2.publish(EndFrame.builder());
        publisher1.publish(BeginFrame.builder());

        verify(endFrameHandlerMock, times(1)).call(any());
        verify(beginFrameFrameHandlerMock, times(1)).call(any());
    }
} 
