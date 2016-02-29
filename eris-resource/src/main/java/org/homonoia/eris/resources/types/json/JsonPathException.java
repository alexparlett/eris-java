package org.homonoia.eris.resources.types.json;

import org.homonoia.eris.resources.exceptions.ResourceException;

/**
 * Created by alexparlett on 17/02/2016.
 */
public class JsonPathException extends ResourceException {
    public JsonPathException(final String msg) {
        super(msg);
    }

    public JsonPathException(final String msg, final Throwable ex) {
        super(msg, ex);
    }
}
