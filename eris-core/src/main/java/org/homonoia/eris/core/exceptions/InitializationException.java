package org.homonoia.eris.core.exceptions;

import org.homonoia.eris.core.Errors;

/**
 * Created by alexparlett on 06/02/2016.
 */
public class InitializationException extends Exception {

    private Errors error;

    public InitializationException(final String msg) {
        this(msg, Errors.UNKNOWN);
    }

    public InitializationException(final String msg, final Errors error) {
        super(msg);
        this.error = error;
    }

    public Errors getError() {
        return error;
    }
}
