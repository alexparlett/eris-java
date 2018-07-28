package org.homonoia.sw.procedural.spec.galaxies;

import com.badlogic.gdx.utils.Array;
import org.homonoia.sw.procedural.Star;
import org.homonoia.sw.procedural.spec.GalaxySpec;
import org.homonoia.sw.procedural.spec.celestials.StarSpec;

import java.util.List;
import java.util.Random;

public class Spiral extends GalaxySpec {

    private int spacing;
    private int arms;
    private float swirl = (float) (Math.PI * 4);

    public Spiral(List<StarSpec> starSpecs) {
        super(starSpecs);
    }

    @Override
    public Array<Star> generate(Random random) {
        return null;
    }
}
