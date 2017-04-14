package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexparlett on 21/05/2016.
 */
public class Stream extends Resource {

    private static final Logger LOG = LoggerFactory.getLogger(Stream.class);

    private FileSystem fileSystem;

    public Stream(final Context context) {
        super(context);
        fileSystem = context.getBean(FileSystem.class);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        setState(AsyncState.SUCCESS);
    }

    @Override
    public void save() throws IOException {
    }

    public InputStream asInputStream() {
        try {
            return fileSystem.newInputStream(getLocation());
        } catch (IOException e) {
            LOG.error("Failed to create InputStream from Stream for {}", getLocation(), e);
            return null;
        }
    }

    public OutputStream asOutputStream() {
        try {
            return fileSystem.newOutputStream(getLocation());
        } catch (IOException e) {
            LOG.error("Failed to create InputStream from Stream for {}", getLocation(), e);
            return null;
        }
    }
}
