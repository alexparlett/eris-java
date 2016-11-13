package org.homonoia.eris.core.collections.pools;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/11/2016
 */
@FunctionalInterface
public interface PoolFactory<T> {
    T create();
}
