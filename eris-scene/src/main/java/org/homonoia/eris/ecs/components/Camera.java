package org.homonoia.eris.ecs.components;

import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.annotations.Requires;
import org.homonoia.eris.graphics.drawables.RenderTarget;
import org.joml.Vector3d;
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
    private double near;
    private double far;
    private double fov;
    private RenderTarget renderTarget;
    private Vector4f backgroundColor;

    public Set<Integer> getLayerMask() {
        return layerMask;
    }

    public double getNear() {
        return near;
    }

    public double getFar() {
        return far;
    }

    public double getFov() {
        return fov;
    }

    public RenderTarget getRenderTarget() {
        return renderTarget;
    }

    public Vector4f getBackgroundColor() {
        return backgroundColor;
    }

    public Camera near(double near) {
        this.near = near;
        return this;
    }

    public Camera far(double far) {
        this.far = far;
        return this;
    }

    public Camera fov(double fov) {
        this.fov = fov;
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
}
