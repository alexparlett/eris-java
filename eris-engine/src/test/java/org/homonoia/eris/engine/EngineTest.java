package org.homonoia.eris.engine;

import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.junit.Ignore;
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
    @Ignore
    public void testEngine() throws InitializationException {

        Path currentDir = FileSystem.getApplicationDirectory();
        System.setProperty("user.dir", currentDir.resolve("../assets").toString());

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(EngineTest.class);

        Engine engine = applicationContext.getBean(Engine.class);
        engine.initialize();
        engine.run();
        engine.shutdown();
    }
}
