package org.homonoia.eris.graphics.drawables.primitives.factory;

import org.homonoia.eris.graphics.drawables.primitives.Primitive;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 05/01/2017
 */
public interface PrimitiveFactory<T extends Primitive> {
    T getObject();
}
