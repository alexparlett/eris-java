package org.homonoia.eris.resources.cache;

import lombok.extern.slf4j.Slf4j;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.Statistics;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.core.utils.ThreadUtils;
import org.homonoia.eris.resources.GPUResource;
import org.homonoia.eris.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 19/02/2016
 */
@Slf4j
class ResourceLoader extends Contextual {


    private final FileSystem fileSystem;
    private final ExecutorService executorService;
    private final Statistics statistics;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public ResourceLoader(final Context context, final FileSystem fileSystem) {
        super(context);
        this.fileSystem = fileSystem;
        statistics = context.getBean(Statistics.class);
        executorService = context.getBean(ExecutorService.class);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void load(final Resource resource, final boolean immediate) throws IOException {
        Objects.requireNonNull(resource, MessageFormat.format("Failed to load {0} resource cannot be null", resource.getPath()));
        Objects.requireNonNull(resource.getLocation(), MessageFormat.format("Failed to load {0} path cannot be null", resource.getPath()));

        if (!fileSystem.isAccessible(resource.getLocation())) {
            throw new IOException(resource.getLocation().toString() + " is not accessible.");
        }

        if (resource.getState().equals(Resource.AsyncState.FAILED) ||
                resource.getState().equals(Resource.AsyncState.SUCCESS) ||
                resource.getState().equals(Resource.AsyncState.LOADING)) {
            return;
        }

        LoadingTask task = new LoadingTask();
        task.resource = resource;
        task.path = resource.getLocation();

        if (ThreadUtils.isMainThread() && resource.getState().equals(Resource.AsyncState.GPU_READY)) {
            ((GPUResource) resource).compile();
        } else if (!immediate) {
            if (!resource.getState().equals(Resource.AsyncState.QUEUED)) {
                resource.setState(Resource.AsyncState.QUEUED);
                executorService.submit(task);
            }
        } else {
            process(task);
        }
    }

    private void process(LoadingTask loadingTask) {
        if (loadingTask.resource != null && loadingTask.path != null) {
            statistics.getCurrent().startSegment();
            loadingTask.resource.setState(Resource.AsyncState.LOADING);
            try (InputStream inputStream = fileSystem.newInputStream(loadingTask.path)) {
                loadingTask.resource.load(inputStream);
                if (ThreadUtils.isMainThread() && loadingTask.resource.getState().equals(Resource.AsyncState.GPU_READY)) {
                    ((GPUResource) loadingTask.resource).compile();
                }
                log.info("Loaded {} {}", loadingTask.resource.getClass().getSimpleName(), loadingTask.path);
            } catch (IOException e) {
                loadingTask.resource.setState(Resource.AsyncState.FAILED);
                log.error("Failed to load {} {}", loadingTask.resource.getClass().getSimpleName(), loadingTask.path, e);
            }
            statistics.getCurrent().endSegment("Resource Loaded " + loadingTask.resource.getClass().getSimpleName());
        }
    }

    private class LoadingTask implements Callable<Resource.AsyncState> {
        private Resource resource;
        private Path path;

        @Override
        public Resource.AsyncState call() {
            if (resource.getState().equals(Resource.AsyncState.QUEUED)) {
                process(this);
            }
            return resource.getState();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LoadingTask that = (LoadingTask) o;

            if (resource != null ? !resource.equals(that.resource) : that.resource != null) return false;
            return !(path != null ? !path.equals(that.path) : that.path != null);

        }

        @Override
        public int hashCode() {
            int result = resource != null ? resource.hashCode() : 0;
            result = 31 * result + (path != null ? path.hashCode() : 0);
            return result;
        }
    }
}
