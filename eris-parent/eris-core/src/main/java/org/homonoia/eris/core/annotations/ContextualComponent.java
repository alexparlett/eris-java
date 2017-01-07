package org.homonoia.eris.core.annotations;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 14/02/2016
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public @interface ContextualComponent {
}
