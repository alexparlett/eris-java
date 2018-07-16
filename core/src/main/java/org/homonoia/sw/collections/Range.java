package org.homonoia.sw.collections;

import lombok.Data;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2018
 */
@Data
public class Range<T extends Number> {
    private T start;
    private T end;
}
