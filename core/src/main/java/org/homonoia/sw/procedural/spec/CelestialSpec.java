package org.homonoia.sw.procedural.spec;

import com.badlogic.gdx.math.Vector3;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.homonoia.sw.collections.Range;

import java.util.List;
import java.util.Random;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
public abstract class CelestialSpec<T> {
    private String name;
    private Range<Float> temperature;
    private Range<Float> mass;
    private Range<Float> radius;
    private List<String> tags;
    private List<String> conditions;

    public abstract T generate(Random random, Vector3 position);
}
