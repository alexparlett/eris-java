package org.homonoia.eris.configuration;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.annotations.ContextualBean;
import org.homonoia.eris.core.components.Clock;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.engine.Engine;
import org.homonoia.eris.engine.Locale;
import org.homonoia.eris.engine.Log;
import org.homonoia.eris.engine.Settings;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.primitives.Cube;
import org.homonoia.eris.graphics.drawables.primitives.PrimitiveFactory;
import org.homonoia.eris.input.Input;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.scripting.ScriptEngine;
import org.homonoia.eris.ui.UI;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
@Configuration
@ComponentScan("org.homonoia.eris")
public class EngineConfiguration {

    @ContextualBean
    public Context context(Environment environment) {
        Context context = new Context();
        context.setDebugEnabled(environment.containsProperty("debug"));
        return context;
    }

    @ContextualBean
    public ResourceCache resourceCache(Context context, FileSystem fileSystem) {
        return new ResourceCache(context, fileSystem);
    }

    @ContextualBean
    public Clock clock(Context context) {
        return new Clock(context);
    }

    @ContextualBean
    public Graphics graphics(Context context, ResourceCache resourceCache) {
        return new Graphics(context, resourceCache);
    }

    @ContextualBean
    public Renderer renderer(Context context, Graphics graphics, ResourceCache resourceCache, PrimitiveFactory<Cube> cubeFactory) {
        return new Renderer(context, graphics, resourceCache, cubeFactory);
    }

    @ContextualBean
    public Engine engine(Context context) {
        return new Engine(context);
    }

    @ContextualBean
    public Locale locale(Context context, ResourceCache resourceCache) {
        return new Locale(context, resourceCache);
    }

    @ContextualBean
    public Settings settings(Context context, ResourceCache resourceCache, FileSystem fileSystem) {
        return new Settings(context, resourceCache, fileSystem);
    }

    @ContextualBean
    public FileSystem fileSystem(Context context) {
        return new FileSystem(context);
    }

    @ContextualBean
    public Log log(Context context) {
        return new Log(context);
    }

    @ContextualBean
    public Input input(Context context, Graphics graphics) {
        return new Input(context, graphics);
    }

    @ContextualBean
    public ScriptEngine scriptEngine(Context context) { return new ScriptEngine(context); }

    @ContextualBean
    public ExecutorService executorService() {
        return Executors.newWorkStealingPool();
    }

    @ContextualBean
    public UI ui(Context context) {
        return new UI(context);
    }
}
