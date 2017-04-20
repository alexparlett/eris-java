package org.homonoia.eris.ecs.components;

import org.homonoia.eris.core.exceptions.ErisRuntimeExcecption;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.annotations.Requires;
import org.homonoia.eris.graphics.drawables.RenderTarget;
import org.homonoia.eris.graphics.drawables.Skybox;
import org.joml.Matrix4f;
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
public class Camera extends Component {

    private Set<Integer> layerMask = new HashSet<>(8);
    private float near;
    private float far;
    private float fov;
    private RenderTarget renderTarget;
    private Vector4f backgroundColor;
    private Skybox skybox;
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public Camera(Context context) {
        super(context);
    }

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

    public Skybox getSkybox() {
        return skybox;
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

    public Camera renderTarget(RenderTarget renderTarget) {
        this.renderTarget = renderTarget;
        return this;
    }

    public Camera backgroundColor(Vector4f backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Camera skybox(Skybox skybox) {
        this.skybox = skybox;
        return this;
    }

    public Matrix4f getViewMatrix() {
        Transform transform = getEntity().get(Transform.class)
                .orElseThrow(() -> new ErisRuntimeExcecption("Camera on Entity {} without Transform", getEntity().getId()));
        return viewMatrix.setLookAt(transform.getTranslation(), transform.forward().add(transform.getTranslation()), transform.up());
    }

    public Matrix4f getProjectionMatrix() {
        float aspectRatio = (float) getRenderTarget().getWidth() / getRenderTarget().getHeight();
        projectionMatrix.setPerspective(getFov(), aspectRatio, getNear(), getFar());
        return projectionMatrix;
    }
}
