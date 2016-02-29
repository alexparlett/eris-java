package org.homonoia.eris.resources.exceptions;

import org.homonoia.eris.core.exceptions.ErisException;

/**
 * Created by alexp on 27/02/2016.
 */
public class ResourceException extends ErisException {
    public ResourceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ResourceException(final String message) {
        super(message);
    }

    public ResourceException(final String msg, final Throwable ex, final Object... args) {
        super(msg, ex, args);
    }

    public ResourceException(final String msg, final Object... args) {
        super(msg, args);

    }
}
