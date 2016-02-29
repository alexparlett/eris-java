package org.homonoia.eris.core.exceptions;

import org.slf4j.helpers.MessageFormatter;

/**
 * Created by alexp on 27/02/2016.
 */
public class ErisRuntimeExcecption extends RuntimeException {
    public ErisRuntimeExcecption(final String message) {
        super(message);
    }

    public ErisRuntimeExcecption(final Throwable throwable) {
        super(throwable);
    }

    public ErisRuntimeExcecption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ErisRuntimeExcecption(final String msg, final Object... args) {
        super(MessageFormatter.arrayFormat(msg, args).getMessage());

    }

    public ErisRuntimeExcecption(final String msg, final Throwable ex, final Object... args) {
        super(MessageFormatter.arrayFormat(msg, args).getMessage(), ex);
    }
}
