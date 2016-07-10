package org.homonoia.eris.ecs.annotations;

import org.homonoia.eris.ecs.Component;

import java.lang.annotation.*;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/07/2016
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Requires {

    Class<? extends Component>[] classes() default {};

    boolean autoAdd() default false;
}
