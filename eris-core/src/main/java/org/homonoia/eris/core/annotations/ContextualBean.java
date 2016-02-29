package org.homonoia.eris.core.annotations;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.*;

/**
 * Created by alexp on 25/02/2016.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public @interface ContextualBean {
}
