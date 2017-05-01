package org.homonoia.sw.scripting;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.janino.CachingJavaSourceClassLoader;
import org.codehaus.janino.util.resource.JarDirectoriesResourceFinder;
import org.codehaus.janino.util.resource.MapResourceCreator;
import org.codehaus.janino.util.resource.MapResourceFinder;
import org.codehaus.janino.util.resource.MultiResourceFinder;
import org.codehaus.janino.util.resource.PathResourceFinder;
import org.codehaus.janino.util.resource.ResourceCreator;
import org.codehaus.janino.util.resource.ResourceFinder;
import org.homonoia.sw.core.exceptions.ErisRuntimeExcecption;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 14/04/2017
 */
@Slf4j
public class ScriptClassLoader {

    private ConcurrentHashMap<String, byte[]> classCache = new ConcurrentHashMap<>();
    private ResourceFinder cacheResourceFinder = new MapResourceFinder(classCache);
    private ResourceCreator cacheResourceCreator = new MapResourceCreator(classCache);
    private ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    @SuppressWarnings("unchecked")
    public <T extends Script> T newInstance(String name) {
        try {
            Class<T> aClass = (Class<T>) classLoader.loadClass(name);
            return aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new ErisRuntimeExcecption("Cannot create Script of {}", e, name);
        }
    }

    public void registerDirectory(Path root) {
        File scriptDirectory = root.resolve("Scripts").toFile();
        if (scriptDirectory.exists()) {
            File[] directories = {scriptDirectory};
            ResourceFinder resourceFinder = new MultiResourceFinder(Arrays.asList(new PathResourceFinder(directories), new JarDirectoriesResourceFinder(directories)));
            classLoader = new CachingJavaSourceClassLoader(classLoader, resourceFinder, null, cacheResourceFinder, cacheResourceCreator);
        }
    }
}
