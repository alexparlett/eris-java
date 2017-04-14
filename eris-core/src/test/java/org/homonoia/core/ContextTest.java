package org.homonoia.core;

import org.hamcrest.CoreMatchers;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.Event;
import org.homonoia.eris.events.core.Pause;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

/**
 * Contextual Tester.
 *
 * @author Alex Parlett
 * @version 1.0
 * @since <pre>Dec 12, 2015</pre>
 */
@RunWith(JUnit4.class)
public class ContextTest {

    @Test
    public void testEventBus_WithoutClass() throws Exception {

        Context publisher = new Context(new CommandLineArgs());

        List<Event> events = new ArrayList<>();
        publisher.subscribe(Pause -> events.add(Pause), Context.eventClassPredicate(null).and(Context.eventSourcePredicate(null)));
        publisher.publish(Pause.builder().build());

        Assert.assertThat(events.size(), CoreMatchers.is(1));
    }

    @Test
    public void testEventBus_WithClass() throws Exception {

        Context publisher = new Context(new CommandLineArgs());

        List<Event> events = new ArrayList<>();
        publisher.subscribe(Pause -> events.add(Pause), Context.eventClassPredicate(Pause.class).and(Context.eventSourcePredicate(null)));
        publisher.publish(Pause.builder().build());

        Assert.assertThat(events.size(), CoreMatchers.is(1));
    }

    @Test
    public void testEventBus_WithSourceNotMatched() throws Exception {

        Context publisher = new Context(new CommandLineArgs());
        Contextual eventSrc = new Contextual(publisher) {
        };

        List<Event> events = new ArrayList<>();
        publisher.subscribe(Pause -> events.add(Pause), Context.eventClassPredicate(Pause.class).and(Context.eventSourcePredicate(eventSrc)));
        publisher.publish(Pause.builder().source(new Object()).build());

        Assert.assertThat(events.size(), CoreMatchers.is(0));
    }

    @Test
    public void testEventBus_WithSourceMatched() throws Exception {

        Context publisher = new Context(new CommandLineArgs());
        Contextual eventSrc = new Contextual(publisher) {
        };

        List<Event> events = new ArrayList<>();
        publisher.subscribe(Pause -> events.add(Pause), Context.eventClassPredicate(Pause.class).and(Context.eventSourcePredicate(eventSrc)));
        publisher.publish(Pause.builder().source(eventSrc).build());

        Assert.assertThat(events.size(), CoreMatchers.is(1));
    }

    @Test
    public void testEventBus_MultipleSubscriptions() throws Exception {

        Context publisher = new Context(new CommandLineArgs());

        List<Event> events = new ArrayList<>();
        publisher.subscribe(Pause -> events.add(Pause), Context.eventClassPredicate(null).and(Context.eventSourcePredicate(null)));
        publisher.subscribe(Pause -> events.add(Pause), Context.eventClassPredicate(null).and(Context.eventSourcePredicate(null)));
        publisher.publish(Pause.builder().build());

        Assert.assertThat(events.size(), CoreMatchers.is(2));
    }

    @Test
    public void testEventBus_MultipleSubscriptions_DontPropagate() throws Exception {

        Context publisher = new Context(new CommandLineArgs());

        List<Event> events = new ArrayList<>();
        publisher.subscribe(Pause -> { events.add(Pause); Pause.stopPropagation();}, Context.eventClassPredicate(null).and(Context.eventSourcePredicate(null)));
        publisher.subscribe(Pause -> events.add(Pause), Context.eventClassPredicate(null).and(Context.eventSourcePredicate(null)));
        publisher.publish(Pause.builder().build());

        Assert.assertThat(events.size(), CoreMatchers.is(1));
    }
} 
