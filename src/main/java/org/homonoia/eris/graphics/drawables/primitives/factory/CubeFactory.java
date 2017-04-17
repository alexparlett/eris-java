package org.homonoia.eris.graphics.drawables.primitives.factory;

import org.homonoia.eris.graphics.drawables.primitives.Cube;

import static java.util.Objects.isNull;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 05/01/2017
 */
public class CubeFactory implements PrimitiveFactory<Cube> {

    private Cube cube;

    @Override
    public Cube getObject() {
        if (isNull(cube)) {
            cube = new Cube();
            cube.compile();
        }

        return cube;
    }
}
