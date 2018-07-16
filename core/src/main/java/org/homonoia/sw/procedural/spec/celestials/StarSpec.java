package org.homonoia.sw.procedural.spec.celestials;

import lombok.Data;
import org.homonoia.sw.procedural.Star;
import org.homonoia.sw.procedural.spec.CelestialSpec;

import java.util.Random;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2018
 */
@Data
public class StarSpec extends CelestialSpec<Star> {
    @Override
    public Star generate(Random random) {
        return null;
    }
}
