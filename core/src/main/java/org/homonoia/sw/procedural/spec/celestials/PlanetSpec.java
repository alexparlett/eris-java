package org.homonoia.sw.procedural.spec.celestials;

import lombok.Data;
import org.homonoia.sw.procedural.Planet;
import org.homonoia.sw.procedural.spec.CelestialSpec;

import java.util.Random;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2018
 */
@Data
public class PlanetSpec extends CelestialSpec<Planet> {
    @Override
    public Planet generate(Random random) {
        return null;
    }
}
