package org.homonoia.eris.resources.types.ini;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 27/02/2016
 */
public class IniSection implements Iterable<Map.Entry<String, String>> {

    private final Map<String, String> keyValues = new HashMap<>();

    public void set(final String key, final String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        keyValues.put(key, value);
    }

    public Optional<String> get(final String key) {
        Objects.requireNonNull(key);
        return Optional.ofNullable(keyValues.get(key));
    }

    public Optional<Integer> getInteger(final String key) {
        return get(key).map(Integer::parseInt);
    }

    public Optional<Double> getDouble(final String key) {
        return get(key).map(Double::parseDouble);
    }

    public Optional<Boolean> getBoolean(final String key) {
        return get(key).map(Boolean::parseBoolean);
    }

    public Optional<Float> getFloat(final String key) {
        return get(key).map(Float::parseFloat);
    }

    public void remove(final String key) {
        Objects.requireNonNull(key);
        keyValues.remove(key);
    }

    public void clear() {
        keyValues.clear();
    }

    public boolean contains(final String key) {
        Objects.requireNonNull(key);
        return keyValues.containsKey(key);
    }

    public boolean isEmpty() {
        return keyValues.isEmpty();
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return keyValues.entrySet().iterator();
    }

    @Override
    public void forEach(final Consumer<? super Map.Entry<String, String>> action) {
        Objects.requireNonNull(action);
        keyValues.entrySet().forEach(action);
    }

    @Override
    public Spliterator<Map.Entry<String, String>> spliterator() {
        return keyValues.entrySet().spliterator();
    }
}


