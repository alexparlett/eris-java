package org.homonoia.eris.core.exceptions;

import org.slf4j.helpers.MessageFormatter;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 27/02/2016
 */
public class ErisException extends Exception {
    public ErisException(final String message) {
        super(message);
    }

    public ErisException(final Throwable throwable) {
        super(throwable);
    }

    public ErisException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ErisException(final String msg, final Object... args) {
        super(MessageFormatter.arrayFormat(msg, args).getMessage());

    }

    public ErisException(final String msg, final Throwable ex, final Object... args) {
        super(MessageFormatter.arrayFormat(msg, args).getMessage(), ex);
    }
}
