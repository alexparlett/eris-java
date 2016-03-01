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
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alexp on 25/02/2016.
 */
@Configuration
public class ComponentConfiguration {

    @ContextualBean
    public Context context() {
        return new Context();
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
    public Graphics graphics(Context context) {
        return new Graphics(context);
    }

    @ContextualBean
    public Renderer renderer(Context context, Graphics graphics) {
        return new Renderer(context, graphics);
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
}