package org.homonoia.sw.state.impl;

import lombok.extern.slf4j.Slf4j;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.ecs.ComponentFactory;
import org.homonoia.eris.ecs.Entity;
import org.homonoia.eris.ecs.EntityManager;
import org.homonoia.eris.ecs.EntitySystemManager;
import org.homonoia.eris.ecs.FamilyManager;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.ecs.components.Mesh;
import org.homonoia.eris.ecs.components.Transform;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.homonoia.eris.ecs.systems.InputSystem;
import org.homonoia.eris.ecs.systems.RenderSystem;
import org.homonoia.eris.ecs.systems.UpdateSystem;
import org.homonoia.eris.events.core.ExitRequested;
import org.homonoia.eris.events.input.KeyDown;
import org.homonoia.eris.events.input.MouseScroll;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.Model;
import org.homonoia.eris.graphics.drawables.Skybox;
import org.homonoia.eris.input.Key;
import org.homonoia.eris.input.Modifier;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.sw.state.State;
import org.joml.Vector4f;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/04/2017
 */
@Slf4j
public class TestState extends Contextual implements State {

    private EntityManager entityManager;
    private FamilyManager familyManager;
    private EntitySystemManager entitySystemManager;
    private ResourceCache resourceCache;
    private Entity model;
    private Entity camera;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public TestState(Context context) {
        super(context);
    }

    @Override
    public void create() {
        entityManager = new EntityManager(getContext());
        familyManager = new FamilyManager(getContext());

        entitySystemManager = new EntitySystemManager(getContext());
        entitySystemManager.add(new InputSystem(getContext(), familyManager));
        entitySystemManager.add(new UpdateSystem(getContext(), familyManager));
        entitySystemManager.add(new RenderSystem(getContext(), familyManager));

        resourceCache = getContext().getBean(ResourceCache.class);
        resourceCache.add(Model.class, "Models/fighter.mdl", false);
        resourceCache.add(Skybox.class, "Skyboxes/spacescape.skybox", false);
    }

    @Override
    public void start() {
        subscribe(this::mouseScrolled, MouseScroll.class);
        subscribe(this::keyPress, KeyDown.class);

        Graphics graphics = getContext().getBean(Graphics.class);
        ComponentFactory componentFactory = getContext().getBean(ComponentFactory.class);

        Model model = resourceCache.get(Model.class, "Models/fighter.mdl").get();
        Skybox skybox = resourceCache.get(Skybox.class, "Skyboxes/spacescape.skybox").get();

        try {
            camera = new Entity(getContext());
            camera.add(componentFactory.newInstance(Transform.class).translate(0, 0, 0).rotate(0, 0, 0));
            camera.add(componentFactory.newInstance(Camera.class)
                    .far(100)
                    .near(5)
                    .fov(60)
                    .backgroundColor(new Vector4f(255, 255, 255, 1))
                    .renderTarget(graphics.getDefaultRenderTarget())
                    .skybox(skybox)
            );
            entityManager.add(camera);

            this.model = new Entity(getContext());
            this.model.add(componentFactory.newInstance(Transform.class).translate(0, 0, -15));
            this.model.add(componentFactory.newInstance(Mesh.class).model(model));
            this.model.add(componentFactory.newInstance("core.ships.Fighter"));
            entityManager.add(this.model);
        } catch (MissingRequiredComponentException e) {
            e.printStackTrace();
        }
    }

    private void keyPress(KeyDown evt) {
        Transform cameraTransform = camera.get(Transform.class).get();
        Transform modelTransform = model.get(Transform.class).get();
        if (evt.getKey().equals(Key.R)) {
            Transform transform = evt.getMods().contains(Modifier.SHIFT) ? modelTransform : cameraTransform;
            transform.rotation(0, 0, 0).translation(0, 0, 0);
        } else if (evt.getKey().equals(Key.ESCAPE)) {
            publish(ExitRequested.builder());
        }
    }

    private void mouseScrolled(MouseScroll evt) {
        Transform cameraTransform = camera.get(Transform.class).get();
        double x = evt.getDelta().x();
        double y = evt.getDelta().y();
        if (Math.abs(y) > Math.abs(x)) {
            cameraTransform.getRotation().rotateX((float) Math.toRadians(y));
        } else {
            cameraTransform.getRotation().rotateY((float) Math.toRadians(x));
        }
    }

    @Override
    public void stop() {
        unsubscribe();
    }

    @Override
    public void delete() {
    }
}
