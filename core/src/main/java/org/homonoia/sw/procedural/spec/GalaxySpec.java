package org.homonoia.sw.procedural.spec;

import com.badlogic.gdx.utils.Array;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.homonoia.sw.procedural.Star;
import org.homonoia.sw.procedural.spec.celestials.StarSpec;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class GalaxySpec {

    protected List<StarSpec> starSpecs;

    public abstract Array<Star> generate(Random random);

    protected Optional<StarSpec> fromTemperature(float kelvin) {
        return starSpecs.stream()
                .filter(starSpec -> starSpec.getTemperature().between(kelvin))
                .findFirst();
    }
}
