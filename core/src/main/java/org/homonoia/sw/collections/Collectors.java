package org.homonoia.sw.collections;

import com.badlogic.gdx.utils.Array;

import java.util.stream.Collector;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
public class Collectors {

    public static <T> Collector<T, ?, Array<T>> toArray() {
        return Collector.of(Array::new, Array::add, (left, right) -> { left.addAll(right); return left;});
    }
}
