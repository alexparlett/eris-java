package org.homonoia.sw.core.exceptions;

import org.slf4j.helpers.MessageFormatter;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 27/02/2016
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
