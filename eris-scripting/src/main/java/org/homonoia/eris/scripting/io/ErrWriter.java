package org.homonoia.eris.scripting.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/11/2016
 */
public class ErrWriter extends Writer {

    private final static Logger LOGGER = LoggerFactory.getLogger("org.homonoia.eris.scripting.err");

    private StringBuilder builder = new StringBuilder();



    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        synchronized (lock) {
            builder.append(cbuf, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        synchronized (lock) {
            if (builder.length() > 0 || builder.charAt(0) != '\n') {
                LOGGER.info(builder.toString());
            }
            builder.delete(0, builder.length());
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            builder.delete(0, builder.length());
            builder = null;
        }
    }
}
