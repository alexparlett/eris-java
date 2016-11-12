package org.homonoia.eris.ecs.components;

import org.joml.Vector3f;
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
                .rotate((float) Math.toRadians(57.3), 0, 0);

        Transform transform = new Transform()
                .setParent(parent)
                .translate(2, 0, 0)
                .translate(1, 0, 0)
                .rotate((float) Math.toRadians(57.3), 0, 0);

//        parent.translate(1,0,0);

        Vector3f translation = transform.getTranslation();
        Vector3f scale = transform.getScale();
        Vector3f rotation = transform.getRotation().getEulerAnglesXYZ(new Vector3f());

        Vector3f localTranslation = transform.getLocalTranslation();
        Vector3f localScale = transform.getLocalScale();
        Vector3f localRotation = transform.getLocalRotation().getEulerAnglesXYZ(new Vector3f());
    }

}
