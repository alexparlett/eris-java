package org.homonoia.eris.ecs.components;

import org.homonoia.eris.core.Context;
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

    public static final Vector3f Up = new Vector3f(0.f, 1.f, 0.f);
    public static final Vector3f Forward = new Vector3f(0.f, 0.f, -1.f);
    public static final Vector3f Right = new Vector3f(1.f, 0.f, 0.f);

    private int layer = 1;

    private Matrix4f transform = new Matrix4f()
            .identity()
            .scale(1)
            .translation(0,0,0)
            .rotationXYZ(0,0,0);

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public Transform(Context context) {
        super(context);
    }

    public Matrix4f get() {
        return transform;
    }


    public Vector3f getTranslation() {
        return get().getTranslation(new Vector3f());
    }

    public Vector3f getScale() {
        return get().getScale(new Vector3f());
    }

    public Quaternionf getRotation() {
        return get().getUnnormalizedRotation(new Quaternionf());
    }


    public Transform translate(Vector3f translationXYZ) {
        return translate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform translate(float x, float y, float z) {
        get().translation(x,y,z);
        return this;
    }

    public Transform rotate(Vector3f translationXYZ) {
        return rotate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform rotate(Quaternionf quat) {
        get().rotate(quat);
        return this;
    }

    public Transform rotate(float x, float y, float z) {
        get().rotateXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
        return this;
    }

    public Transform scale(float xyz) {
        get().scale(xyz).setTranslation(getTranslation().mul(xyz));
        return this;
    }

    public Transform translation(Vector3f translationXYZ) {
        return translation(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform translation(float x, float y, float z) {
        get().setTranslation(x,y,z);
        return this;
    }

    public Transform rotation(Vector3f translationXYZ) {
        return rotation(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform rotation(float x, float y, float z) {
        get().setRotationXYZ(x,y,z);
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
        return get().positiveY(new Vector3f());
    }

    public Vector3f right() {
        return get().positiveX(new Vector3f());
    }

    public Vector3f forward() {
        return get().positiveZ(new Vector3f()).negate();
    }

    public int getLayer() {
        return layer;
    }
}
