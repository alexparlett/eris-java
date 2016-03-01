package org.homonoia.eris.resources.types.image;

import org.homonoia.eris.resources.exceptions.ResourceException;
import org.slf4j.helpers.MessageFormatter;

/**
 * Created by alexparlett on 17/02/2016.
 */
public class ImageException extends ResourceException {
    public ImageException(final String msg) {
        super(msg);
    }

    public ImageException(final String msg, final Throwable ex) {
        super(msg, ex);
    }

    public ImageException(final String msg, final Throwable ex, final Object... args) {
        super(MessageFormatter.arrayFormat(msg, args).getMessage(), ex);
    }

    public ImageException(final String msg, final Object... args) {
        super(MessageFormatter.arrayFormat(msg, args).getMessage());
    }
}