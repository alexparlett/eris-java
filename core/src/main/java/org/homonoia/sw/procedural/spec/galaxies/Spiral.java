package org.homonoia.sw.procedural.spec.galaxies;

import org.homonoia.sw.procedural.Galaxy;
import org.homonoia.sw.procedural.spec.GalaxySpec;

import java.util.Random;

public class Spiral extends GalaxySpec {

    private int spacing;
    private int minArms;
    private int maxAms;
    private float swirl = (float) (Math.PI * 4);

    @Override
    public Galaxy generate(Random random) {
        return null;
    }
}
