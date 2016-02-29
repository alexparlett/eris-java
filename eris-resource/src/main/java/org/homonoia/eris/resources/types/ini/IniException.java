package org.homonoia.eris.resources.types.ini;

import org.homonoia.eris.resources.exceptions.ResourceException;

/**
 * Created by alexparlett on 17/02/2016.
 */
public class IniException extends ResourceException {
    public IniException(final String msg) {
        super(msg);
    }

    public IniException(final String msg, final Throwable ex) {
        super(msg, ex);
    }

    public IniException(final String msg, final Throwable ex, final Object... args) {
       super(msg, ex, args);
    }

    public IniException(final String msg, final Object... args) {
        super(msg, args);
    }
}
