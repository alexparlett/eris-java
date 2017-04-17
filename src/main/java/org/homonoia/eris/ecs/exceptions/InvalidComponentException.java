package org.homonoia.eris.ecs.exceptions;

import org.homonoia.eris.core.exceptions.ErisRuntimeExcecption;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/07/2016
 */
public class InvalidComponentException extends ErisRuntimeExcecption {
    public InvalidComponentException(String msg, Throwable cause, Object... args) {
        super(msg, cause, args);
    }
}
