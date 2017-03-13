package org.homonoia.eris.graphics.drawables.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.homonoia.eris.graphics.drawables.Model;
import org.homonoia.eris.resources.types.mesh.Vertex;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 12/03/2017
 */
@Getter
@EqualsAndHashCode
@ToString
public class AxisAlignedBoundingBox {

    private Vector3f min;
    private Vector3f max;

    private AxisAlignedBoundingBox() {
    }

    public static AxisAlignedBoundingBox generate(Model model) {
        AxisAlignedBoundingBox aabb = new AxisAlignedBoundingBox();
        Vector3f point = new Vector3f();
        float[] points = new float[Vertex.COUNT];

        model.getSubModels().forEach(subModel -> {
            FloatBuffer vertices = subModel.getVertices();
            while (vertices.hasRemaining()) {
                vertices.get(points);
                point.set(points[0], points[1], points[2]);

                if (nonNull(aabb.min)) {
                    aabb.min.min(point);
                } else {
                    aabb.min = new Vector3f(point);
                }

                if (nonNull(aabb.max)) {
                    aabb.max.max(point);
                } else {
                    aabb.max = new Vector3f(point);
                }
            }
            vertices.position(0);
        });

        return aabb;
    }
}
