package org.homonoia.eris.resources.exceptions;

import org.slf4j.helpers.MessageFormatter;

/**
 * Created by alexparlett on 17/02/2016.
 */
public class JsonException extends Exception {
    public JsonException(final String msg) {
        super(msg);
    }

    public JsonException(final String msg, final Throwable ex) {
        super(msg, ex);
    }

    public JsonException(final String msg, final Throwable ex, final Object... args) {
        super(MessageFormatter.arrayFormat(msg, args).getMessage(), ex);
    }

    public JsonException(final String msg, final Object... args) {
        super(MessageFormatter.arrayFormat(msg, args).getMessage());
    }
}
