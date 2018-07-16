package org.homonoia.sw.procedural.spec;

import lombok.AccessLevel;
import lombok.Getter;
import org.homonoia.sw.procedural.Galaxy;

import java.util.Random;

@Getter(AccessLevel.PROTECTED)
public abstract class GalaxySpec {

    private int numberOfSystems;

    public abstract Galaxy generate(Random random);
}
