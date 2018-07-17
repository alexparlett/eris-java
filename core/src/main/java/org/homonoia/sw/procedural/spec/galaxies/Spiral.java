package org.homonoia.sw.procedural.spec.galaxies;

import com.badlogic.gdx.utils.Array;
import org.homonoia.sw.procedural.Star;
import org.homonoia.sw.procedural.spec.GalaxySpec;

import java.util.Random;

public class Spiral extends GalaxySpec {

    private int spacing;
    private int arms;
    private float swirl = (float) (Math.PI * 4);

    @Override
    public Array<Star> generate(Random random) {
        return null;
    }
}
