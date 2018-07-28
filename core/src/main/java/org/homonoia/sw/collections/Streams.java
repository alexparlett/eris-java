package org.homonoia.sw.collections;

import com.badlogic.gdx.utils.Array;

import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
public class Streams {

    public static <T> Stream<T> of(Array<T> array) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(array.iterator(), 0), false);
    }

}
