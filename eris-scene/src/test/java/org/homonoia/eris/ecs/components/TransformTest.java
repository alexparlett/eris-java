package org.homonoia.eris.ecs.components;

import org.joml.Vector3d;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class TransformTest {

    @Test
    public void testTransform_WithParent() {
        Transform parent = new Transform()
                .translation(10, 0, 0)
                .scale(10)
                .translate(10,0,0)
                .rotate(Math.toRadians(57.3), 0, 0);

        Transform transform = new Transform()
                .setParent(parent)
                .translate(2, 0, 0)
                .translate(1, 0, 0)
                .rotate(Math.toRadians(57.3), 0, 0);

//        parent.translate(1,0,0);

        Vector3d translation = transform.getTranslation();
        Vector3d scale = transform.getScale();
        Vector3d rotation = transform.getRotation().getEulerAnglesXYZ(new Vector3d());

        Vector3d localTranslation = transform.getLocalTranslation();
        Vector3d localScale = transform.getLocalScale();
        Vector3d localRotation = transform.getLocalRotation().getEulerAnglesXYZ(new Vector3d());
    }

}
