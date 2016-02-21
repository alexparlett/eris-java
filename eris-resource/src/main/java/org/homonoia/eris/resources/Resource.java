package org.homonoia.eris.resources;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by alexparlett on 05/02/2016.
 */
public abstract class Resource extends Contextual {

    public enum AsyncState {
        NEW,
        QUEUED,
        LOADING,
        SUCCESS,
        FAILED
    }

    private Path path;
    private AtomicReference<AsyncState> state = new AtomicReference<>(AsyncState.NEW);

    public Resource(final Context context) {
        super(context);
    }

    public Path getPath() {
        return path;
    }

    public AsyncState getState() {
        return state.get();
    }

    public void setPath(final Path path) {
        this.path = path;
    }

    public void setState(final AsyncState state) {
        this.state.set(state);
    }

    public abstract void load(InputStream inputStream) throws IOException;
    public abstract void save(OutputStream outputStream) throws IOException;
}
