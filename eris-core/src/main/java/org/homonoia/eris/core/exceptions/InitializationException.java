package org.homonoia.eris.core.exceptions;

import org.homonoia.eris.core.ExitCode;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public class InitializationException extends ErisException {

    private final ExitCode error;

    public InitializationException(final String msg) {
        this(msg, ExitCode.UNKNOWN);
    }

    public InitializationException(final String msg, final ExitCode error) {
        super(msg);
        this.error = error;
    }

    public ExitCode getError() {
        return error;
    }
}
