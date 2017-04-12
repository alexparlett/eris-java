package org.homonoia.sw;

import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.engine.Engine;
import org.homonoia.eris.engine.Log;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.stream.Stream;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 12/04/2017
 */
@Configuration
@ComponentScan("org.homonoia")
@EnableAsync
public class SolarianWarsMain {
    public static void main(String[] args) throws InitializationException {
        boolean debug = Stream.of(args).anyMatch(s -> "--debug".equals(s));
        Log.initialize(debug);

        CommandLinePropertySource clps = new SimpleCommandLinePropertySource(args);

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().getPropertySources().addFirst(clps);
        applicationContext.register(SolarianWarsMain.class);
        applicationContext.refresh();

        Engine engine = applicationContext.getBean(Engine.class);
        engine.initialize();
        engine.run();
        engine.shutdown();
    }
}
