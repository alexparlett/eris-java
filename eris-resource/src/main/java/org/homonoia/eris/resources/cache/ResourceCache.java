package org.homonoia.eris.resources.cache;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.Image;
import org.homonoia.eris.resources.types.Ini;
import org.homonoia.eris.resources.types.Json;
import org.homonoia.eris.resources.types.Mesh;
import org.homonoia.eris.resources.types.Python;
import org.homonoia.eris.resources.types.Stream;
import org.homonoia.eris.resources.types.ini.IniSection;
import org.homonoia.eris.resources.types.json.JsonPatch;
import org.homonoia.eris.resources.types.json.JsonPath;
import org.homonoia.eris.resources.types.json.JsonType;
import org.homonoia.eris.resources.types.mesh.Face;
import org.homonoia.eris.resources.types.mesh.Vertex;
import org.homonoia.eris.scripting.ScriptBinding;
import org.homonoia.eris.scripting.ScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 19/02/2016
 */
public class ResourceCache extends Contextual implements ScriptBinding {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceCache.class);

    private final Map<Class<? extends Resource>, Map<Path, Resource>> groups = new HashMap<>();
    private final List<Path> directories = new ArrayList<>();
    private final ResourceLoader loader;
    private final FileSystem fileSystem;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public ResourceCache(final Context context, final FileSystem fileSystem) {
        super(context);
        context.registerBean(this);
        this.loader = new ResourceLoader(context, fileSystem);
        this.fileSystem = fileSystem;
    }

    public void shutdown() {
        loader.shutdown();
        clear(true);
    }

    public boolean addDirectory(final Path path) {
        return addDirectory(path, Integer.MAX_VALUE);
    }

    public boolean addDirectory(final String path) {
        return addDirectory(Paths.get(path));
    }

    public boolean addDirectory(final Path path, final int priority) {
        LOG.debug("Adding Directory {} to Resource Cache Lookup.", path);
        if (fileSystem.isAccessible(path)) {
            if (priority < directories.size()) {
                directories.add(priority, path);
            } else {
                directories.add(path);
            }
            return true;
        }

        return false;
    }

    public boolean addDirectory(final String path, final int priority) {
        return addDirectory(Paths.get(path), priority);
    }

    public boolean removeDirectory(final Path path) {
        return directories.remove(path);
    }

    public boolean removeDirectory(final String path) {
        return removeDirectory(Paths.get(path));
    }

    public <T extends Resource> Optional<T> get(final Class<T> clazz, final Path path) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(clazz);

        T resource = find(clazz, path).map((res) -> (T) res).orElseGet(() -> {
            add(clazz, path, true);
            return find(clazz, path).map((res) -> (T) res).orElse(null);
        });

        if (resource != null && resource.getState().equals(Resource.AsyncState.SUCCESS)) {
            return Optional.of(resource.hold());
        } else if (resource != null && resource.getState().equals(Resource.AsyncState.LOADING)) {
            while (resource.getState().equals(Resource.AsyncState.LOADING)) ;
            if (resource.getState().equals(Resource.AsyncState.SUCCESS))
                return Optional.of(resource.hold());
        } else if (resource != null && !resource.getState().equals(Resource.AsyncState.FAILED)) {
            try {
                loader.load(resource, true);
                if (resource.getState().equals(Resource.AsyncState.SUCCESS)) {
                    return Optional.of(resource.hold());
                }
            } catch (IOException ex) {
                LOG.error("Failed to load Resource", ex);
            }
        }

        // Resource == Null || Resource State == FAILED
        return Optional.empty();
    }

    public <T extends Resource> Optional<T> get(final Class<T> clazz, final String path) {
        return get(clazz, Paths.get(path));
    }

    public <T extends Resource> Optional<T> getTemporary(final Class<T> clazz, final Path path) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(clazz);

        Path relativePath = path;
        if (path.isAbsolute()) {
            for (Path directory : directories) {
                if (path.startsWith(directory)) {
                    relativePath = path.relativize(directory);
                    break;
                }
            }
        }

        T resource = find(clazz, relativePath).map(res -> (T) res).orElseGet(() -> {
            try {
                Path fullPath = findFile(path).orElseThrow(() -> new IOException("Resource does not exist."));
                T newResource = clazz.getConstructor(Context.class).newInstance(getContext());
                newResource.setPath(path);
                newResource.setLocation(fullPath);
                return newResource;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
                LOG.error("Failed to create new {} {}.", clazz, path, e);
                return null;
            }
        });

        if (resource != null && resource.getState().equals(Resource.AsyncState.SUCCESS)) {
            return Optional.of(resource);
        } else if (resource != null && resource.getState().equals(Resource.AsyncState.LOADING)) {
            while (resource.getState().equals(Resource.AsyncState.LOADING)) ;
            if (resource.getState().equals(Resource.AsyncState.SUCCESS))
                return Optional.of(resource);
        } else if (resource != null && !resource.getState().equals(Resource.AsyncState.FAILED)) {
            try {
                loader.load(resource, true);
                if (resource.getState().equals(Resource.AsyncState.SUCCESS)) {
                    return Optional.of(resource);
                }
            } catch (IOException ex) {
                LOG.error("Failed to load Resource", ex);
            }
        }

        // Resource == Null || Resource State == FAILED
        return Optional.empty();
    }

    public <T extends Resource> Optional<T> getTemporary(final Class<T> clazz, final String path) {
        return getTemporary(clazz, Paths.get(path));
    }

    public synchronized void remove(final Class<? extends Resource> clazz, final Path path, boolean force) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(clazz);

        Map<Path, ? extends Resource> group = groups.get(clazz);
        if (group != null) {
            Resource remove = group.get(path);
            if (Objects.nonNull(remove) && (remove.getRefCount() <= 1 || force)) {
                remove.release();
                group.remove(path);
            }
        }
    }

    public synchronized void remove(final Class<? extends Resource> clazz, final String path, boolean force) {
        remove(clazz, Paths.get(path), force);
    }

    public synchronized void remove(final Class<? extends Resource> clazz, boolean force) {
        Objects.requireNonNull(clazz);
        Map<Path, Resource> group = groups.get(clazz);
        if (Objects.nonNull(group)) {
            Iterator<Map.Entry<Path, Resource>> iterator = group.entrySet().iterator();
            iterator.forEachRemaining(entry -> {
                if (Objects.nonNull(entry.getValue()) && (entry.getValue().getRefCount() <= 1 || force)) {
                    entry.getValue().release();
                    iterator.remove();
                }
            });
        }
        if (group.isEmpty()) {
            groups.remove(clazz);
        }
    }

    public synchronized void clear(boolean force) {
        Iterator<Map.Entry<Class<? extends Resource>, Map<Path, Resource>>> groupsIterator = groups.entrySet().iterator();
        groupsIterator.forEachRemaining(classMapEntry ->  {
            Iterator<Map.Entry<Path, Resource>> groupIterator = classMapEntry.getValue().entrySet().iterator();
            groupIterator.forEachRemaining(entry -> {
                if (Objects.nonNull(entry.getValue()) && (entry.getValue().getRefCount() <= 1 || force)) {
                    entry.getValue().release();
                    groupIterator.remove();
                }
            });
            if (classMapEntry.getValue().isEmpty()) {
                groupsIterator.remove();
            }
        });
    }

    public void add(final Class<? extends Resource> clazz, final Path path, final boolean immediate) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(clazz);

        try {
            if (find(clazz, path).isPresent()) {
                return;
            }

            Path fullPath = findFile(path).orElseThrow(() -> new IOException("Resource does not exist."));

            Resource resource = clazz.getConstructor(Context.class).newInstance(getContext());
            resource.setPath(path);
            resource.setLocation(fullPath);

            synchronized (this) {
                Map<Path, Resource> group = groups.get(clazz);
                if (group == null) {
                    group = new HashMap<>();
                    group.put(path, resource);
                    groups.put(clazz, group);
                } else {
                    group.put(path, resource);
                }
            }

            loader.load(resource, immediate);
        } catch (IOException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error("Failed to add {} {} to Resource Cache", clazz.getSimpleName(), path, e);
        }
    }

    public void add(final Class<? extends Resource> clazz, final String path, final boolean immediate) {
        add(clazz, Paths.get(path), immediate);
    }

    private synchronized Optional<Resource> find(final Class<? extends Resource> clazz, final Path path) {
        Map<Path, ? extends Resource> group = groups.get(clazz);
        if (group != null) {
            Resource resource = group.get(path);
            if (resource != null && resource.getState() != Resource.AsyncState.FAILED) {
                return Optional.of(resource);
            }
        }
        return Optional.empty();
    }

    private synchronized Optional<Resource> find(final Class<? extends Resource> clazz, final String path) {
        return find(clazz, Paths.get(path));
    }

    private Optional<Path> findFile(final Path path) throws IOException {
        if (path == null) {
            throw new IOException("Path must not be null.");
        }

        for (Path directory : directories) {
            Path fullPath = directory.resolve(path);
            if (fullPath.isAbsolute() && Files.exists(fullPath)) {
                return Optional.of(fullPath);
            }
        }
        return Optional.empty();
    }

    @Override
    public void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindClass(JsonPatch.class);
        scriptEngine.bindClass(JsonPath.class);
        scriptEngine.bindClass(JsonType.class);
        scriptEngine.bindClass(IniSection.class);
        scriptEngine.bindClass(Face.class);
        scriptEngine.bindClass(Vertex.class);
        scriptEngine.bindClass(Image.class);
        scriptEngine.bindClass(Ini.class);
        scriptEngine.bindClass(Json.class);
        scriptEngine.bindClass(Mesh.class);
        scriptEngine.bindClass(Stream.class);
        scriptEngine.bindClass(Python.class);
        scriptEngine.bindGlobal("resourceCache", this);
    }
}
