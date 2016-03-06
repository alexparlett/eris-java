package org.homonoia.eris.resources.types.json;

import org.homonoia.eris.resources.exceptions.ResourceException;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 17/02/2016
 */
public class JsonPathException extends ResourceException {
    public JsonPathException(final String msg) {
        super(msg);
    }

    public JsonPathException(final String msg, final Throwable ex) {
        super(msg, ex);
    }
}
