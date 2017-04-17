package org.homonoia.eris.resources.cache;

import lombok.extern.slf4j.Slf4j;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.FileSystem;
import org.homonoia.eris.core.Statistics;
import org.homonoia.eris.resources.GPUResource;
import org.homonoia.eris.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;
import static org.homonoia.eris.core.utils.ThreadUtils.isMainThread;
import static org.homonoia.eris.resources.Resource.AsyncState;
import static org.homonoia.eris.resources.Resource.AsyncState.FAILED;
import static org.homonoia.eris.resources.Resource.AsyncState.GPU_READY;
import static org.homonoia.eris.resources.Resource.AsyncState.LOADING;
import static org.homonoia.eris.resources.Resource.AsyncState.QUEUED;
import static org.homonoia.eris.resources.Resource.AsyncState.SUCCESS;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/04/2017
 */
@Slf4j()
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
        requireNonNull(resource, format("Failed to load {0} resource cannot be null", resource.getPath()));
        requireNonNull(resource.getLocation(), format("Failed to load {0} path cannot be null", resource.getPath()));

        if (!fileSystem.isAccessible(resource.getLocation())) {
            throw new IOException(resource.getLocation().toString() + " is not accessible.");
        }

        if (resource.getState().equals(FAILED) ||
                resource.getState().equals(SUCCESS) ||
                resource.getState().equals(LOADING)) {
            return;
        }

        LoadingTask task = new LoadingTask();
        task.resource = resource;
        task.path = resource.getLocation();

        if (isMainThread() && resource.getState().equals(GPU_READY)) {
            ((GPUResource) resource).compile();
        } else if (!immediate) {
            if (!resource.getState().equals(QUEUED)) {
                resource.setState(QUEUED);
                executorService.submit(task);
            }
        } else {
            process(task);
        }
    }

    private void process(LoadingTask loadingTask) {
        if (loadingTask.resource != null && loadingTask.path != null) {
            statistics.getCurrent().startSegment();
            loadingTask.resource.setState(LOADING);
            try (InputStream inputStream = fileSystem.newInputStream(loadingTask.path)) {
                loadingTask.resource.load(inputStream);
                if (isMainThread() && loadingTask.resource.getState().equals(GPU_READY)) {
                    ((GPUResource) loadingTask.resource).compile();
                }
                log.info("Loaded {} {}", loadingTask.resource.getClass().getSimpleName(), loadingTask.path);
            } catch (IOException e) {
                loadingTask.resource.setState(FAILED);
                log.error("Failed to load {} {}", loadingTask.resource.getClass().getSimpleName(), loadingTask.path, e);
            }
            statistics.getCurrent().endSegment("Resource Loaded " + loadingTask.resource.getClass().getSimpleName());
        }
    }

    private class LoadingTask implements Callable<AsyncState> {
        private Resource resource;
        private Path path;

        @Override
        public AsyncState call() {
            if (resource.getState().equals(QUEUED)) {
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
