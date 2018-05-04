package org.homonoia.sw.mvc.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Inject;
import org.homonoia.sw.ecs.components.CameraComponent;
import org.homonoia.sw.ecs.components.ModelComponent;
import org.homonoia.sw.ecs.components.RigidBodyComponent;
import org.homonoia.sw.ecs.components.TransformComponent;
import org.homonoia.sw.ecs.core.Engine;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.ecs.core.Family;
import org.homonoia.sw.ecs.systems.impl.RenderingSystem;
import org.homonoia.sw.ecs.utils.ImmutableArray;
import org.homonoia.sw.graphics.ModelService;
import org.homonoia.sw.mvc.component.ui.InterfaceService;
import org.homonoia.sw.mvc.stereotype.View;
import org.homonoia.sw.physics.MotionState;

/**
 * Thanks to View annotation, this class will be automatically found and initiated.
 * <p>
 * This is application's main view, displaying a menu with several options.
 */
@View(id = "game", value = "ui/templates/game.lml", themes = "music/theme.ogg")
public class GameController extends AshleyView {

    @Inject
    private InterfaceService interfaceService;

    @Inject
    private ModelService modelService;
    private ImmutableArray<Entity> cameras;


    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        super.initialize(stage, actorMappedByIds);


        Engine engine = getEngine();
        cameras = engine.getEntitiesFor(Family.all(CameraComponent.class).get());

        engine.addSystem(new RenderingSystem());

        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        Matrix4 position = new Matrix4();
        ModelInstance modelInstance = new ModelInstance(model);
        MotionState motionState = new MotionState(position);

        ModelComponent modelComponent = engine.createComponent(ModelComponent.class, modelInstance);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class, motionState);
        RigidBodyComponent rigidBodyComponent = engine.createComponent(RigidBodyComponent.class,1.f, modelInstance, motionState);

        Entity objectEntity = engine.createEntity(modelComponent, transformComponent, rigidBodyComponent);
        engine.addEntity(objectEntity);


        PerspectiveCamera perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(10, 10, 10);
        perspectiveCamera.lookAt(0,0,0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();
        CameraComponent cameraComponent = engine.createComponent(CameraComponent.class, perspectiveCamera);
        Entity cameraEntity = engine.createEntity(cameraComponent);
        engine.addEntity(cameraEntity);
    }

    @Override
    public void show(Stage stage, Action action) {
        super.show(stage, action);

    }

    @Override
    public void resize(Stage stage, int width, int height) {
        cameras.stream()
                .map(entity -> entity.getComponent(CameraComponent.class))
                .map(CameraComponent::getCamera)
                .forEach(camera -> {
                    camera.viewportWidth = width;
                    camera.viewportHeight = height;
                    camera.update();
                });
    }
}