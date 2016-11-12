package org.homonoia.eris.core.collections;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 10/07/2016
 */
public interface Pool<T> {

    T obtain();

    void free(T object);

    void free(T... objects);

    void reset(T object);

    void clear();
}
