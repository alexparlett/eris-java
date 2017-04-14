package org.homonoia.eris.scripting;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.janino.CachingJavaSourceClassLoader;
import org.codehaus.janino.util.resource.JarDirectoriesResourceFinder;
import org.codehaus.janino.util.resource.MapResourceCreator;
import org.codehaus.janino.util.resource.MapResourceFinder;
import org.codehaus.janino.util.resource.MultiResourceFinder;
import org.codehaus.janino.util.resource.PathResourceFinder;
import org.codehaus.janino.util.resource.ResourceCreator;
import org.codehaus.janino.util.resource.ResourceFinder;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.resource.DirectoryAdded;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 14/04/2017
 */
@Slf4j
public class ScriptEngine extends Contextual {

    private ConcurrentHashMap<String, byte[]> classCache;
    private ResourceFinder cacheResourceFinder;
    private ResourceCreator cacheResourceCreator;
    private ClassLoader classLoader;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public ScriptEngine(Context context) {
        super(context);
        context.registerBean(this);
        subscribe(this::handleDirectoryAdded, DirectoryAdded.class);
    }

    public void initialize() {
        classCache = new ConcurrentHashMap<>();
        cacheResourceFinder = new MapResourceFinder(classCache);
        cacheResourceCreator = new MapResourceCreator(classCache);
    }

    private void handleDirectoryAdded(DirectoryAdded evt) {
        File scriptDirectory = evt.getPath().resolve("Scripts").toFile();
        if (scriptDirectory.exists()) {
            File[] directories = {scriptDirectory};
            ResourceFinder resourceFinder = new MultiResourceFinder(Arrays.asList(new PathResourceFinder(directories), new JarDirectoriesResourceFinder(directories)));
            if (nonNull(classLoader)) {
                classLoader = new CachingJavaSourceClassLoader(classLoader, resourceFinder, null, cacheResourceFinder, cacheResourceCreator);

            } else {
                classLoader = new CachingJavaSourceClassLoader(ClassLoader.getSystemClassLoader(), resourceFinder, null, cacheResourceFinder, cacheResourceCreator);
            }
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }
}
