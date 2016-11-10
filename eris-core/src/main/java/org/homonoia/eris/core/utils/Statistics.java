package org.homonoia.eris.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 08/11/2016
 */
public class Statistics {

    private Map<String, Object> values = new HashMap<>();

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "values=" + values +
                '}';
    }
}
