package org.homonoia.eris.engine;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.ecs.Entity;
import org.homonoia.eris.ecs.EntityManager;
import org.homonoia.eris.ecs.components.Camera;
import org.homonoia.eris.ecs.components.Mesh;
import org.homonoia.eris.ecs.components.Transform;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.Model;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.joml.Vector4f;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.nio.file.Path;

/**
 * Created by alexparlett on 11/04/2016.
 */
@Configuration
@ComponentScan("org.homonoia.eris")
@EnableAsync
public class EngineTest {

    @Test
    public void testEngine() throws InitializationException, MissingRequiredComponentException {

        Path currentDir = FileSystem.getApplicationDirectory();
        System.setProperty("user.dir", currentDir.resolve("../assets").toString());

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(EngineTest.class);


        Engine engine = applicationContext.getBean(Engine.class);
        engine.initialize();

        Context context = applicationContext.getBean(Context.class);
        ResourceCache resourceCache = applicationContext.getBean(ResourceCache.class);
        EntityManager entityManager = applicationContext.getBean(EntityManager.class);
        Graphics graphics = applicationContext.getBean(Graphics.class);

        Model model = resourceCache.get(Model.class, "Models/planet.mdl").get();

        Entity entity0 = new Entity(context);
        entity0.add(new Transform().translate(0,0,0));
        entity0.add(new Camera().far(100).near(1).fov(55).backgroundColor(new Vector4f(0,0,0,1)).renderTarget(graphics.getDefaultRenderTarget()));
        entityManager.add(entity0);

        Entity entity1 = new Entity(context);
        entity1.add(new Transform().translate(0,0,-10));
        entity1.add(new Mesh().model(model));
        entityManager.add(entity1);

        engine.run();
        engine.shutdown();
    }
}
