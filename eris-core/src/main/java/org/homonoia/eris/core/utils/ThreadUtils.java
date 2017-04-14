package org.homonoia.eris.core.utils;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 14/04/2017
 */
public class ThreadUtils {
    public static boolean isMainThread(){
        return Thread.currentThread().getId() == 1L;
    }
}
