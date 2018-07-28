package org.homonoia.sw.scene.controllers.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.homonoia.sw.ecs.components.CameraComponent;
import org.homonoia.sw.ecs.components.ModelComponent;
import org.homonoia.sw.ecs.components.PointLightComponent;
import org.homonoia.sw.ecs.components.RigidBodyComponent;
import org.homonoia.sw.ecs.components.TransformComponent;
import org.homonoia.sw.ecs.core.Engine;
import org.homonoia.sw.ecs.core.Entity;
import org.homonoia.sw.ecs.systems.impl.EnvironmentSystem;
import org.homonoia.sw.ecs.systems.impl.PhysicsSystem;
import org.homonoia.sw.ecs.systems.impl.RenderingSystem;
import org.homonoia.sw.physics.KinematicMotionState;
import org.homonoia.sw.scene.World;
import org.homonoia.sw.scene.WorldController;
import org.homonoia.sw.service.AssetService;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage;
import static com.badlogic.gdx.physics.bullet.collision.CollisionConstants.DISABLE_DEACTIVATION;
import static com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT;
import static com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
public class MapWorldController extends WorldController {
    @Override
    public void create(AssetService assetService) {
        Model ogexTest = assetService.finishLoading("shipsets/mechanica/civilian_colony.ogex", Model.class);

        PerspectiveCamera perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(10, 10, 10);
        perspectiveCamera.lookAt(0,0,0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();

        world = new World(new ScreenViewport(perspectiveCamera), new CameraInputController(perspectiveCamera));

        Engine engine = world.getEngine();

        Environment environment = world.getEnvironment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));

        DebugDrawer debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(DBG_MAX_DEBUG_DRAW_MODE);

        engine.addSystem(new EnvironmentSystem(environment));
        engine.addSystem(new PhysicsSystem(debugDrawer));
        engine.addSystem(new RenderingSystem(environment, debugDrawer));

        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                Usage.Position | Usage.Normal);

        ogexTest.materials.add(new Material(ColorAttribute.createDiffuse(Color.GREEN)));

        Matrix4 position = new Matrix4();
        ModelInstance modelInstance = new ModelInstance(ogexTest, position);
        KinematicMotionState motionState = new KinematicMotionState(position);

        ModelComponent modelComponent = engine.createComponent(ModelComponent.class, modelInstance);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class, motionState);
        RigidBodyComponent rigidBodyComponent = engine.createComponent(RigidBodyComponent.class,1.f, modelInstance, motionState);

        rigidBodyComponent.getRigidBody().setCollisionFlags(rigidBodyComponent.getRigidBody().getCollisionFlags() | CF_KINEMATIC_OBJECT);
        rigidBodyComponent.getRigidBody().setActivationState(DISABLE_DEACTIVATION);

        Entity objectEntity = engine.createEntity(modelComponent, transformComponent, rigidBodyComponent);
        engine.addEntity(objectEntity);


        PointLightComponent pointLightComponent = engine.createComponent(PointLightComponent.class, Color.WHITE, new Vector3(-10,10,10), 1000, environment);
        Entity lightEntity = engine.createEntity(pointLightComponent);
        engine.addEntity(lightEntity);

        CameraComponent cameraComponent = engine.createComponent(CameraComponent.class, perspectiveCamera,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Entity cameraEntity = engine.createEntity(cameraComponent);
        engine.addEntity(cameraEntity);
    }
}
