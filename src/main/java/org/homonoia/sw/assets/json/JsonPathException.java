package org.homonoia.sw.assets.json;

import org.homonoia.sw.core.exceptions.ErisException;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 17/02/2016
 */
public class JsonPathException extends ErisException {
    public JsonPathException(final String msg) {
        super(msg);
    }

    public JsonPathException(final String msg, final Throwable ex) {
        super(msg, ex);
    }
}
