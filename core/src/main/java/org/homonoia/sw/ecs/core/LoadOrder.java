package org.homonoia.sw.ecs.core;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/04/2018
 */
public @interface LoadOrder {
    int value() default 0;
}
