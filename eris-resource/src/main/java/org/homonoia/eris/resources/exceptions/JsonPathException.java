package org.homonoia.eris.resources.exceptions;

/**
 * Created by alexparlett on 17/02/2016.
 */
public class JsonPathException extends Exception {
    public JsonPathException(final String msg) {
        super(msg);
    }

    public JsonPathException(final String msg, final Throwable ex) {
        super(msg, ex);
    }
}
