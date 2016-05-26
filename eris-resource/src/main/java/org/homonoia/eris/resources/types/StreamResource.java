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
public class StreamResource extends Resource {

    private static final Logger LOG = LoggerFactory.getLogger(StreamResource.class);

    private FileSystem fileSystem;

    public StreamResource(final Context context) {
        super(context);
        fileSystem = context.getBean(FileSystem.class);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {

    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
    }

    public InputStream asInputStream() {
        try {
            return fileSystem.newInputStream(getPath());
        } catch (IOException e) {
            LOG.error("Failed to create InputStream from StreamResource for {}", getPath(), e);
            return null;
        }
    }

    public OutputStream asOutputStream() {
        try {
            return fileSystem.newOutputStream(getPath());
        } catch (IOException e) {
            LOG.error("Failed to create InputStream from StreamResource for {}", getPath(), e);
            return null;
        }
    }
}
