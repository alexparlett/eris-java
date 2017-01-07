package org.homonoia.eris.core.annotations;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.*;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public @interface ContextualBean {
}
