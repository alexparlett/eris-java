package org.homonoia.sw.assets.json;

import org.homonoia.sw.core.exceptions.ErisException;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 17/02/2016
 */
public class JsonException extends ErisException {
    public JsonException(final String msg) {
        super(msg);
    }

    public JsonException(final String msg, final Throwable ex) {
        super(msg, ex);
    }

    public JsonException(final String msg, final Throwable ex, final Object... args) {
        super(msg, ex, args);
    }

    public JsonException(final String msg, final Object... args) {
        super(msg, args);
    }
}
