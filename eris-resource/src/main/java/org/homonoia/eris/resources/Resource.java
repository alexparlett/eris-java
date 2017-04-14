package org.homonoia.eris.resources;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 05/02/2016
 */
public abstract class Resource extends Contextual implements Closeable {

    private AtomicInteger refCount = new AtomicInteger(1);

    public enum AsyncState {
        NEW,
        QUEUED,
        LOADING,
        GPU_READY,
        SUCCESS,
        FAILED
    }

    private Path path;
    private Path location;
    private final AtomicReference<AsyncState> state = new AtomicReference<>(AsyncState.NEW);

    public Resource(final Context context) {
        super(context);
    }

    public Path getPath() {
        return path;
    }

    public Path getLocation() {
        return location;
    }

    public void setLocation(Path location) {
        this.location = location;
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

    public <T extends Resource> T hold() {
        this.refCount.getAndIncrement();
        return (T) this;
    }

    public void release() {
        int refCount = this.refCount.decrementAndGet();
        if (refCount <= 0) {
            reset();
        }
    }

    public void reset() {}

    public int getRefCount() {
        return refCount.get();
    }

    @Override
    public void close() throws IOException {
        release();
    }

    public abstract void load(InputStream inputStream) throws IOException;

    public void save() throws IOException {
        throw new UnsupportedOperationException();
    }
}
