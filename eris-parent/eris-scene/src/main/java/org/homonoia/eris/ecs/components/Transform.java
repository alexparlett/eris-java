package org.homonoia.eris.ecs.components;

import org.homonoia.eris.ecs.Component;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;

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

    private static final ThreadLocal<Matrix4f> tempTransform = ThreadLocal.withInitial(() -> new Matrix4f());

    private Transform parent;
    private Set<Transform> children = new HashSet<>();
    private int layer = 0;

    private Matrix4f transform = new Matrix4f()
            .identity()
            .scale(1)
            .translation(0,0,0)
            .rotationXYZ(0,0,0);

    public Matrix4f get() {
        return transform;
    }

    protected Transform set(final Matrix4f transform) {
        getChildren().forEach((child) -> child.set(transform.mul(child.getLocal(), child.get())));
        this.transform.set(transform);
        return this;
    }

    public Transform getParent() {
        return parent;
    }

    public Transform setParent(final Transform parent) {
        this.set(parent.get().mul(getLocal(), tempTransform.get()));
        this.parent = parent;
        this.parent.getChildren().add(this);
        return this;
    }

    public Set<Transform> getChildren() {
        return children;
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

    public Matrix4f getLocal() {
        return nonNull(parent) ? parent.get()
                .invert(new Matrix4f())
                .mul(transform) : transform;
    }

    public Vector3f getLocalTranslation() {
        return getLocal().getTranslation(new Vector3f());
    }

    public Vector3f getLocalScale() {
        return getLocal().getScale(new Vector3f());
    }

    public Quaternionf getLocalRotation() {
        return getLocal().getUnnormalizedRotation(new Quaternionf());
    }

    public Transform translate(Vector3f translationXYZ) {
        return translate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform translate(float x, float y, float z) {
        return set(get().translate(x,y,z, tempTransform.get()));
    }

    public Transform rotate(Vector3f translationXYZ) {
        return rotate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform rotate(Quaternionf quat) {
        return set(get().rotate(quat));
    }

    public Transform rotate(float x, float y, float z) {
        return set(get().rotateXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z), tempTransform.get()));
    }

    public Transform scale(float xyz) {
        return set(get().scale(xyz, tempTransform.get()).setTranslation(getTranslation().mul(xyz)));
    }

    public Transform translation(Vector3f translationXYZ) {
        return translation(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform translation(float x, float y, float z) {
        return set(tempTransform.get().set(transform).setTranslation(x,y,z));
    }

    public Transform rotation(Vector3f translationXYZ) {
        return rotation(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform rotation(float x, float y, float z) {
        return set(tempTransform.get().set(transform).setRotationXYZ(x,y,z));
    }

    public Transform layer(int layer) {
        if (layer < 0 || layer > 31) {
            throw new IllegalArgumentException("Layer must be between 0 (inclusive) and 32");
        }
        this.layer = layer;
        return this;
    }

    public Vector3f up() {
        return getRotation().getEulerAnglesXYZ(new Vector3f()).mul(Up);
    }

    public Vector3f right() {
        return getRotation().getEulerAnglesXYZ(new Vector3f()).mul(Right);
    }

    public Vector3f forward() {
        return getRotation().getEulerAnglesXYZ(new Vector3f()).mul(Forward);
    }

    public int getLayer() {
        return layer;
    }
}
