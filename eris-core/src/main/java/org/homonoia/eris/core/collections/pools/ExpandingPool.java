package org.homonoia.eris.core.collections.pools;

import org.homonoia.eris.core.collections.Pool;
import org.homonoia.eris.core.collections.Poolable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 10/07/2016
 */
public class ExpandingPool<T> implements Pool<T> {

    private int max;
    private Class<T> type;
    private Stack<T> available;

    public ExpandingPool(final int initialSize, final int max, Class<T> type, Object... args) {
        try {
            this.available = new Stack<>();
            this.available.addAll(Collections.nCopies(initialSize, newObject(args)));
        } catch (IllegalAccessException|InstantiationException|NoSuchMethodException|InvocationTargetException e) {
            throw new RuntimeException("No valid no args constructor found for " + type.getName());
        }
        this.max = max;
        this.type = type;
    }

    @Override
    public T obtain(Object... args) {
        try {
            return available.isEmpty() ? newObject(args) : available.pop();
        } catch (IllegalAccessException|InstantiationException|NoSuchMethodException|InvocationTargetException e) {
            throw new RuntimeException("No valid no args constructor found for " + type.getName());
        }
    }

    @Override
    public void free(final T object) {
        requireNonNull(object);
        if (available.size() < max) {
            available.push(object);
        }
        reset(object);
    }

    @Override
    public void free(final T... objects) {
        Arrays.stream(objects).forEach(this::free);
    }

    @Override
    public void reset(final T object) {
        if (object instanceof Poolable) ((Poolable) object).reset();
    }

    @Override
    public void clear() {
        available.clear();
    }

    protected T newObject(Object... args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return type.getConstructor(Stream.of(args)
                .map(Object::getClass)
                .collect(Collectors.toList()).toArray(new Class[]{}))
                .newInstance(args);
    }
}
