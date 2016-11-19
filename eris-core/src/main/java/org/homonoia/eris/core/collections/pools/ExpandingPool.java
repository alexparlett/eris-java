package org.homonoia.eris.core.collections.pools;

import org.homonoia.eris.core.collections.Pool;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;
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
    private Queue<T> available;

    public ExpandingPool(final int initialSize, final int max, PoolFactory<T> factory) {
        this.max = max;
        this.factory = factory;
        this.available = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < initialSize; i++) {
            this.available.add(newObject());
        }
    }

    @Override
    public T obtain() {
        T t = available.poll();
        if (isNull(t)) {
            t = newObject();
        }
        return t;
    }

    @Override
    public void free(final T object) {
        requireNonNull(object);
        if (available.size() < max) {
            available.add(object);
        }

        if (object instanceof Resetable) {
            ((Resetable) object).reset();
        }
    }

    @Override
    public void free(final T... objects) {
        Arrays.stream(objects).forEach(this::free);
    }

    @Override
    public void clear() {
        available.clear();
    }

    protected T newObject() {
        return factory.create();
    }
}
