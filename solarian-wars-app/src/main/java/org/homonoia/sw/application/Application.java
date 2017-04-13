package org.homonoia.sw.application;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.engine.Engine;
import org.homonoia.sw.state.events.StateChange;
import org.homonoia.sw.state.events.StateCreate;
import org.homonoia.sw.state.impl.TestState;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/04/2017
 */
public class Application extends Contextual {

    private Engine engine;
    private StateMachine stateMachine;

    public Application(CommandLineArgs commandLineArgs) {
        super(new Context(commandLineArgs));
        engine = new Engine(getContext());
        stateMachine = new StateMachine(getContext());
    }

    public void startup() throws InitializationException {
        engine.initialize();

        publish(StateCreate.builder()
                .id(1L)
                .state(new TestState(getContext())));

        publish(StateChange.builder()
                .id(1L));
    }

    public void run() {
        engine.run();
    }

    public void shutdown() {
        engine.shutdown();
    }
}
