package org.homonoia.eris.graphics.drawables.primitives;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 05/01/2017
 */
@Component
@Scope(SCOPE_SINGLETON)
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

    @Override
    public Class<?> getObjectType() {
        return Cube.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
