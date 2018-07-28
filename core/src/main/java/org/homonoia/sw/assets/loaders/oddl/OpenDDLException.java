package org.homonoia.sw.assets.loaders.oddl;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class OpenDDLException extends GdxRuntimeException {
    public OpenDDLException(final String s) {
        super(s);
    }

    public OpenDDLException(final Throwable throwable) {
        super(throwable);
    }

    public OpenDDLException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
}
