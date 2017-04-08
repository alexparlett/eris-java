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
public class BoundingBoxFactory implements PrimitiveFactory<BoundingBox> {

    private BoundingBox boundingBox;

    @Override
    public BoundingBox getObject() {
        if (isNull(boundingBox)) {
            boundingBox = new BoundingBox();
            boundingBox.compile();
        }

        return boundingBox;
    }

    @Override
    public Class<?> getObjectType() {
        return BoundingBox.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
