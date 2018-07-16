package org.homonoia.sw.procedural.spec.celestials;

import com.badlogic.gdx.math.Vector3;
import lombok.Data;
import org.homonoia.sw.procedural.Star;
import org.homonoia.sw.procedural.spec.CelestialSpec;

import java.util.Random;

import static org.homonoia.sw.procedural.BlackBodySpectrum.kelvinToColor;
import static org.homonoia.sw.utils.RandomUtils.random;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2018
 */
@Data
public class StarSpec extends CelestialSpec<Star> {

    @Override
    public Star generate(Random random, Vector3 position) {
        return Star.builder()
                .position(position)
                .color(kelvinToColor(random(random, getTemperature().getStart(), getTemperature().getEnd())))
                .build();
    }
}
