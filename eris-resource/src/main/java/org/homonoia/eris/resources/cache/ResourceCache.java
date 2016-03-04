package org.homonoia.eris.resources.cache;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.io.FileSystem;
import org.homonoia.eris.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by alexparlett on 19/02/2016.
 */
@ContextualComponent
public class ResourceCache extends Contextual {

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
    @Autowired
    public ResourceCache(final Context context, final FileSystem fileSystem) {
        super(context);
        this.loader = new ResourceLoader(context, fileSystem);
        this.fileSystem = fileSystem;
    }

    public void shutdown() {
        loader.shutdown();
        clear();
    }

    public boolean addDirectory(final Path path) {
        return addDirectory(path, Integer.MAX_VALUE);
    }

    public boolean addDirectory(final Path path, final int priority) {
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

    public boolean removeDirectory(final Path path) {
        return directories.remove(path);
    }

    public <T extends Resource> Optional<T> get(final Class<T> clazz, final Path path) {
        return get(clazz, path, true);
    }

    public <T extends Resource> Optional<T> get(final Class<T> clazz, final Path path, boolean errorOnFail) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(clazz);

        T resource = find(clazz, path).map((res) -> (T) res).orElseGet(() -> {
            add(clazz, path, true);
            return find(clazz, path).map((res) -> (T) res).orElse(null);
        });

        if (resource != null && resource.getState().equals(Resource.AsyncState.SUCCESS)) {
            return Optional.of(resource);
        }
        else if (resource != null && resource.getState().equals(Resource.AsyncState.LOADING))
        {
            while (resource.getState().equals(Resource.AsyncState.LOADING));
            if (resource.getState().equals(Resource.AsyncState.SUCCESS))
                return Optional.of(resource);
        } else if (resource != null && !resource.getState().equals(Resource.AsyncState.FAILED)) {
            try {
                Path fullPath = findFile(path).orElseThrow(() -> new IOException("Resource does not exist."));
                loader.load(resource, fullPath, true);
            } catch (IOException ex) {

            }
        }

        // Resource == Null || Resource State == FAILED
        return Optional.empty();
    }

    public <T extends Resource> Optional<T> getTemporary(final Class<T> clazz, final Path path) {
        return getTemporary(clazz, path, false);
    }

    public <T extends Resource> Optional<T> getTemporary(final Class<T> clazz, final Path path, boolean errorOnFail) {
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
                T newResource = clazz.getConstructor(Context.class).newInstance(getContext());
                newResource.setPath(path);
                return newResource;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOG.error("Failed to create new {} {}.", clazz, path, e);
                return null;
            }
        });

        if (resource != null && resource.getState().equals(Resource.AsyncState.SUCCESS)) {
            return Optional.of(resource);
        }
        else if (resource != null && resource.getState().equals(Resource.AsyncState.LOADING))
        {
            while (resource.getState().equals(Resource.AsyncState.LOADING));
            if (resource.getState().equals(Resource.AsyncState.SUCCESS))
                return Optional.of(resource);
        } else if (resource != null && !resource.getState().equals(Resource.AsyncState.FAILED)) {
            try {
                Path fullPath = findFile(path).orElseThrow(() -> new IOException("Resource does not exist."));
                loader.load(resource, fullPath, true);
            } catch (IOException ex) {

            }
        }

        // Resource == Null || Resource State == FAILED
        return Optional.empty();
    }

    public synchronized void remove(final Class<? extends Resource> clazz, final Path path) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(clazz);

        Map<Path, ? extends Resource> group = groups.get(clazz);
        if (group != null) {
            group.remove(path);
        }
    }

    public synchronized void remove(final Class<? extends Resource> clazz) {
        Objects.requireNonNull(clazz);
        groups.remove(clazz);
    }

    public synchronized void clear() {
        groups.clear();
    }

    public void add(final Class<? extends Resource> clazz, final Path path, final boolean immediate) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(clazz);

        try {
            if (find(clazz, path).isPresent()) {
                return;
            }

            Resource resource = clazz.getConstructor(Context.class).newInstance(getContext());
            resource.setPath(path);

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

            Path fullPath = findFile(path).orElseThrow(() -> new IOException("Resource does not exist."));
            loader.load(resource, fullPath, immediate);
        } catch (IOException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error("Failed to add {} {} to Resource Cache", clazz.getSimpleName(), path, e);
        }
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
}
