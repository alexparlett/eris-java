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
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.Model;
import org.homonoia.eris.graphics.drawables.Skybox;
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
    }

    @Override
    public void start() {
        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);
        Graphics graphics = getContext().getBean(Graphics.class);
        ComponentFactory componentFactory = getContext().getBean(ComponentFactory.class);

        Model model = resourceCache.get(Model.class, "Models/fighter.mdl").get();
        Skybox skybox = resourceCache.get(Skybox.class, "Skyboxes/spacescape.skybox").get();

        try {
            Entity entity0 = new Entity(getContext());
            entity0.add(new Transform().rotate(0, 0, 0));
            entity0.add(new Camera()
                    .far(100)
                    .near(5)
                    .fov(55)
                    .backgroundColor(new Vector4f(255, 255, 255, 1))
                    .renderTarget(graphics.getDefaultRenderTarget())
                    .skybox(skybox)
            );
            entityManager.add(entity0);

            Entity entity1 = new Entity(getContext());
            entity1.add(componentFactory.newInstance(Transform.class).translate(0, 0, -15));
            entity1.add(componentFactory.newInstance(Mesh.class).model(model));
            entity1.add(componentFactory.newInstance("core.ships.Fighter"));
            entityManager.add(entity1);
        } catch (MissingRequiredComponentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void delete() {

    }
}
