package org.homonoia.sw.assets.loaders.ogex.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Data
@AllArgsConstructor
public class FloatMetric extends Metric {
    private float value;

    @Override
    public boolean isFloat() {
        return true;
    }

    @Override
    public boolean isString() {
        return false;
    }
}
