package org.homonoia.sw.collections;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
public class ArrayUtils {
    /** @return an array of the unboxed values from the given values */
    public static long[] unbox(Long[] values) {
        long[] unboxed = new long[values.length];
        for(int i = 0; i < unboxed.length; i++)
            unboxed[i] = values[i];
        return unboxed;
    }
}
