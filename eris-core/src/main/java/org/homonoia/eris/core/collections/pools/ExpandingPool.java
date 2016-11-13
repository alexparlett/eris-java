package org.homonoia.eris.core.collections.pools;

import org.homonoia.eris.core.collections.Pool;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 10/07/2016
 */
public final class ExpandingPool<T> implements Pool<T> {

    private int max;
    private PoolFactory<T> factory;
    private Stack<T> available;

    public ExpandingPool(final int initialSize, final int max, PoolFactory<T> factory) {
        this.max = max;
        this.factory = factory;
        this.available = new Stack<>();
        this.available.addAll(Collections.nCopies(initialSize, newObject()));
    }

    @Override
    public synchronized T obtain() {
        return available.isEmpty() ? newObject() : available.pop();
    }

    @Override
    public synchronized void free(final T object) {
        requireNonNull(object);
        if (available.size() < max) {
            available.push(object);
        }

        if (object instanceof Resetable) {
            ((Resetable) object).reset();
        }
    }

    @Override
    public synchronized void free(final T... objects) {
        Arrays.stream(objects).forEach(this::free);
    }

    @Override
    public synchronized void clear() {
        available.clear();
    }

    protected T newObject() {
        return factory.create();
    }
}
