package org.homonoia.eris.ecs.components;

import org.homonoia.eris.ecs.Component;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/07/2016
 */
public class Transform extends Component {

    private int layer = 1;
    private Vector3f position = new Vector3f(0);
    private Quaternionf rotation = new Quaternionf();
    private Vector3f scale = new Vector3f(1);

    public Vector3f getTranslation() {
        return position;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Transform translate(Vector3f translationXYZ) {
        return translate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform translate(float x, float y, float z) {
        position.add(x,y,z);
        return this;
    }

    public Transform rotate(Vector3f translationXYZ) {
        return rotate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform rotate(float x, float y, float z) {
        rotation.rotateXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
        return this;
    }

    public Transform scale(float xyz) {
        scale.set(xyz,xyz,xyz);
        return this;
    }

    public Transform translation(Vector3f translationXYZ) {
        return translation(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform translation(float x, float y, float z) {
        position.set(x,y,z);
        return this;
    }

    public Transform rotation(Vector3f translationXYZ) {
        return rotation(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform rotation(float x, float y, float z) {
        rotation.set((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
        return this;
    }

    public Transform layer(int layer) {
        if (layer <= 0 || layer >= 31) {
            throw new IllegalArgumentException("Layer must be between 0 and 32 (inclusive)");
        }
        this.layer = layer;
        return this;
    }

    public Vector3f up() {
        return rotation.positiveY(new Vector3f());
    }

    public Vector3f right() {
        return rotation.positiveX(new Vector3f());
    }

    public Vector3f forward() {
        return rotation.positiveZ(new Vector3f()).negate();
    }

    public int getLayer() {
        return layer;
    }

    public Matrix4f getModelMatrix(Matrix4f dest) {
        return dest.identity()
                .scale(scale)
                .rotate(rotation)
                .translate(position);
    }
}
