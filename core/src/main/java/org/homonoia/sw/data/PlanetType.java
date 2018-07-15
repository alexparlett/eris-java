package org.homonoia.sw.data;

import lombok.Data;

import java.util.List;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2018
 */
@Data
public class PlanetType {
    String name;
    Range<Float> temperature;
    Range<Float> mass;
    Range<Float> radius;
    List<String> tags;
    List<String> conditions;
}
