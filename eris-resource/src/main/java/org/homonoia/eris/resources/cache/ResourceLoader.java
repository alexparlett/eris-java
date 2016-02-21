package org.homonoia.eris.resources.cache;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alexparlett on 19/02/2016.
 */
public class ResourceLoader extends Contextual {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public ResourceLoader(final Context context) {
        super(context);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void load(final Resource resource, final Path fullPath, final boolean immediate) throws IOException {
        Objects.requireNonNull(resource, MessageFormat.format("Failed to load {0} resource cannot be null", resource.getPath()));
        Objects.requireNonNull(fullPath, MessageFormat.format("Failed to load {0} path cannot be null", resource.getPath()));

        if (resource.getState().equals(Resource.AsyncState.FAILED) ||
                resource.getState().equals(Resource.AsyncState.SUCCESS) ||
                resource.getState().equals(Resource.AsyncState.LOADING)) {
            return;
        }

        LoadingTask task = new LoadingTask();
        task.resource = resource;
        task.path = fullPath;

        if (!immediate) {
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
            loadingTask.resource.setState(Resource.AsyncState.LOADING);
            try (InputStream inputStream = Files.newInputStream(loadingTask.path)) {
                loadingTask.resource.load(inputStream);
                loadingTask.resource.setState(Resource.AsyncState.SUCCESS);
                LOG.info("Loaded {} {}", loadingTask.resource.getClass().getSimpleName(), loadingTask.resource.getPath());
            } catch (IOException e) {
                loadingTask.resource.setState(Resource.AsyncState.FAILED);
                LOG.error("Failed to load {} {}", loadingTask.resource.getClass().getSimpleName(), loadingTask.resource.getPath(), e);
            }
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
