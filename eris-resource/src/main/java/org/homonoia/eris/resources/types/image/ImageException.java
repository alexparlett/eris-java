package org.homonoia.eris.resources.types.image;

import org.homonoia.eris.resources.exceptions.ResourceException;
import org.slf4j.helpers.MessageFormatter;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 17/02/2016
 */
public class ImageException extends ResourceException {
    public ImageException(final String msg) {
        super(msg);
    }

    public ImageException(final String msg, final Throwable ex) {
        super(msg, ex);
    }

    public ImageException(final String msg, final Throwable ex, final Object... args) {
        super(MessageFormatter.format(msg, args).getMessage(), ex);
    }

    public ImageException(final String msg, final Object... args) {
        super(MessageFormatter.format(msg, args).getMessage());
    }
}
