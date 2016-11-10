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

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        LOGGER.error(String.valueOf(cbuf));
    }

    @Override
    public void flush() throws IOException {
        //No op
    }

    @Override
    public void close() throws IOException {
        //No op
    }
}
