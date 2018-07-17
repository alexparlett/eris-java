package org.homonoia.sw.procedural.spec.galaxies;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import lombok.NonNull;
import org.homonoia.sw.procedural.Star;
import org.homonoia.sw.procedural.spec.GalaxySpec;
import org.homonoia.sw.procedural.spec.celestials.StarSpec;

import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static org.homonoia.sw.utils.RandomUtils.normallyDistributedSingle;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 17/07/2018
 */
public class Disk extends GalaxySpec {

    @NonNull
    private int size;

    private float densityMean = 0.0000025f;
    private float densityDeviation = 0.000001f;

    private float deviationX = 0.0000025f;
    private float deviationZ = 0.0000025f;

    public Disk(List<StarSpec> starSpecs, int size) {
        this(starSpecs, size,  0.0000025f, 0.000001f, 0.0000025f, 0.0000025f);
    }

    public Disk(List<StarSpec> starSpecs, int size, float densityMean, float densityDeviation, float deviationX, float deviationZ) {
        super(starSpecs);
        this.size = size;
        this.densityMean = densityMean;
        this.densityDeviation = densityDeviation;
        this.deviationX = deviationX;
        this.deviationZ = deviationZ;
    }

    @Override
    public Array<Star> generate(Random random) {
        Array<Star> stars = new Array();

        float density = max(0, normallyDistributedSingle(random, densityDeviation, densityMean));
        int countMax = max(0, (int) (size * size * size * density));
        if (countMax <= 0)
            return stars;

        int count = random.nextInt(countMax);

        while (stars.size < count) {
            Vector3 pos = new Vector3(
                    normallyDistributedSingle(random, deviationX * size, 0),
                    0.f,
                    normallyDistributedSingle(random, deviationZ * size, 0)
            );
            float distance = pos.len() / size;
            float mean = distance * 2000 + (1 - distance) * 15000;
            float kelvin = normallyDistributedSingle(random, 4000, mean, 2400, 40000);

            fromTemperature(kelvin).ifPresent(starSpec -> stars.add(starSpec.generate(random, pos, kelvin)));
        }

        return stars;
    }
}
