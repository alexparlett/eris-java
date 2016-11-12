package org.homonoia.eris.ecs.components;

import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.annotations.Requires;
import org.homonoia.eris.graphics.drawables.RenderTarget;
import org.joml.Vector4f;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/07/2016
 */
@Requires(classes = {Transform.class})
public class Camera implements Component {

    private Set<Integer> layerMask = new HashSet<>();
    private float near;
    private float far;
    private float fov;
    private RenderTarget renderTarget;
    private Vector4f backgroundColor;
    private float aspect;

    public Set<Integer> getLayerMask() {
        return layerMask;
    }

    public float getNear() {
        return near;
    }

    public float getFar() {
        return far;
    }

    public float getFov() {
        return fov;
    }

    public RenderTarget getRenderTarget() {
        return renderTarget;
    }

    public Vector4f getBackgroundColor() {
        return backgroundColor;
    }

    public Camera near(float near) {
        this.near = near;
        return this;
    }

    public Camera far(float far) {
        this.far = far;
        return this;
    }

    public Camera fov(float fov) {
        this.fov = (float) Math.toRadians(fov);
        return this;
    }

    public Camera aspect(float aspect) {
        this.aspect = aspect;
        return this;
    }

    public Camera renderTarget(RenderTarget renderTarget) {
        this.renderTarget = renderTarget;
        return this;
    }

    public Camera backgroundColor(Vector4f backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public float getAspect() {
        return aspect;
    }
}
