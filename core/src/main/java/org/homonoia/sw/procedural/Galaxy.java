package org.homonoia.sw.procedural;

import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import org.homonoia.sw.procedural.spec.GalaxySpec;

import java.util.Random;
import java.util.concurrent.Callable;

public class Galaxy {

    @Getter
    private Array<Star> stars;

    private Galaxy(Array<Star> stars) {
        this.stars = stars;
    }

    public static Callable<Galaxy> generate(GalaxySpec spec, Random random) {
        return () -> new Galaxy(spec.generate(random));
    }
}
