package org.homonoia.eris.resources.types.json;

import org.homonoia.eris.resources.exceptions.ResourceException;

/**
 * Created by alexparlett on 17/02/2016.
 */
public class JsonException extends ResourceException {
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
