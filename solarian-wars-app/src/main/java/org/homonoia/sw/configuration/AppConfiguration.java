package org.homonoia.sw.configuration;

import org.homonoia.eris.configuration.EngineConfiguration;
import org.homonoia.eris.core.Context;
import org.homonoia.sw.application.StateMachine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/04/2017
 */
@Configuration
@Import(EngineConfiguration.class)
@ComponentScan("org.homonoia.sw")
public class AppConfiguration {
    @Bean
    public StateMachine stateMachine(Context context) {
        return new StateMachine(context);
    }
}
