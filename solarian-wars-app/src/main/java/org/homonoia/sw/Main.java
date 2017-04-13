package org.homonoia.sw;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.homonoia.eris.engine.Log;
import org.homonoia.sw.application.Application;
import org.homonoia.sw.configuration.AppConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.stream.Stream;

/**
 * Copyright (c) 20152017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 12/04/2017
 */
public class Main {
    public static void main(String[] args) throws InitializationException, MissingRequiredComponentException {
        boolean debug = Stream.of(args).anyMatch(s -> "--debug".equals(s));
        Log.initialize(debug);

        CommandLinePropertySource clps = new SimpleCommandLinePropertySource(args);

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().getPropertySources().addFirst(clps);
        applicationContext.register(AppConfiguration.class);
        applicationContext.refresh();

        Application application = new Application(applicationContext.getBean(Context.class));
        application.startup();
        application.run();
        application.shutdown();
    }
}
