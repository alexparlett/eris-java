package org.homonoia.eris.ecs.exceptions;

import org.homonoia.eris.core.exceptions.ErisException;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 11/11/2016
 */
public class RenderingException extends ErisException {
    public RenderingException(String msg, Throwable e) {
        super(msg, e);
    }
}
