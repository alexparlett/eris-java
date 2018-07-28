package org.homonoia.sw.assets.loaders.ogex.metrics;

import lombok.Data;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Data
public abstract class Metric {
    public abstract boolean isFloat();
    public abstract boolean isString();
}
