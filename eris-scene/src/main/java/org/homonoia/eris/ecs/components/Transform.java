package org.homonoia.eris.ecs.components;

import org.homonoia.eris.ecs.Component;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * Created by alexparlett on 31/05/2016.
 */
public class Transform implements Component {

    public static final Vector3f Up = new Vector3f(0.f, 1.f, 0.f);
    public static final Vector3f Forward = new Vector3f(0.f, 0.f, -1.f);
    public static final Vector3f Right = new Vector3f(1.f, 0.f, 0.f);

    private static final ThreadLocal<Matrix4d> tempTransform = ThreadLocal.withInitial(() -> new Matrix4d());

    private Transform parent;
    private Set<Transform> children = new HashSet<>();

    private Matrix4d transform = new Matrix4d()
            .identity()
            .scale(1)
            .translation(0,0,0)
            .rotationXYZ(0,0,0);

    protected Matrix4d get() {
        return transform;
    }

    protected Transform set(final Matrix4d transform) {
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

    public Vector3d getTranslation() {
        return get().getTranslation(new Vector3d());
    }

    public Vector3d getScale() {
        return get().getScale(new Vector3d());
    }

    public Quaterniond getRotation() {
        return get().getUnnormalizedRotation(new Quaterniond());
    }

    public Matrix4d getLocal() {
        return nonNull(parent) ? parent.get()
                .invert(new Matrix4d())
                .mul(transform) : transform;
    }

    public Vector3d getLocalTranslation() {
        return getLocal().getTranslation(new Vector3d());
    }

    public Vector3d getLocalScale() {
        return getLocal().getScale(new Vector3d());
    }

    public Quaterniond getLocalRotation() {
        return getLocal().getUnnormalizedRotation(new Quaterniond());
    }

    public Transform translate(Vector3d translationXYZ) {
        return translate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform translate(double x, double y, double z) {
        return set(get().translate(x,y,z, tempTransform.get()));
    }

    public Transform rotate(Vector3d translationXYZ) {
        return rotate(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform rotate(Quaterniond quat) {
        return set(get().rotate(quat));
    }

    public Transform rotate(double x, double y, double z) {
        return set(get().rotateXYZ(x, y, z, tempTransform.get()));
    }

    public Transform scale(double xyz) {
        return set(get().scale(xyz, tempTransform.get()).setTranslation(getTranslation().mul(xyz)));
    }

    public Transform translation(Vector3d translationXYZ) {
        return translation(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform translation(double x, double y, double z) {
        return set(tempTransform.get().set(transform).setTranslation(x,y,z));
    }

    public Transform rotation(Vector3d translationXYZ) {
        return rotation(translationXYZ.x, translationXYZ.y, translationXYZ.z);
    }

    public Transform rotation(double x, double y, double z) {
        return set(tempTransform.get().set(transform).setRotationXYZ(x,y,z));
    }
}
