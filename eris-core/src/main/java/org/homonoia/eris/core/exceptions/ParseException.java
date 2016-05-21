package org.homonoia.eris.core.exceptions;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class ParseException extends ErisRuntimeExcecption {
    public ParseException(final String message) {
        super(message);
    }

    public ParseException(final Throwable throwable) {
        super(throwable);
    }

    public ParseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParseException(final String msg, final Object... args) {
        super(msg, args);
    }

    public ParseException(final String msg, final Throwable ex, final Object... args) {
        super(msg, ex, args);
    }
}
