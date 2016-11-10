package org.homonoia.eris.ecs.systems;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.ecs.Entity;
import org.homonoia.eris.ecs.EntitySystem;
import org.homonoia.eris.ecs.Family;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.ecs.components.Mesh;
import org.homonoia.eris.ecs.components.Transform;
import org.homonoia.eris.events.frame.Update;
import org.homonoia.eris.graphics.drawables.RenderTarget;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.renderer.commands.ClearColorCommand;
import org.joml.FrustumIntersection;
import org.joml.FrustumRayBuilder;
import org.joml.Vector4f;

import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2016
 */
public class RenderSystem extends EntitySystem {

    private Renderer renderer;
    private Family cameraFamily;
    private Family renderableFamily;

    public RenderSystem(final Context context) {
        super(context, MIN_PRIORITY);
        this.cameraFamily = familyManager.get(Camera.class);
        this.renderableFamily = familyManager.get(Mesh.class);
        this.renderer = this.getContext().getBean(Renderer.class);
    }

    @Override
    public void update(final Update update) {
    }

    private void renderCamera(Camera camera) {
    }

    private void renderEntity(Camera camera, Entity entity) {
    }
}
