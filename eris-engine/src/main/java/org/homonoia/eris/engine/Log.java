package org.homonoia.eris.engine;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.components.FileSystem;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 01/03/2016
 */
public class Log extends Contextual {

    public Log(final Context context) {
        super(context);
        context.registerBean(this);
    }

    public static void initialize(boolean debug) {
        System.setProperty("log.APP_DIR", FileSystem.getApplicationDataDirectory().toString());
        System.setProperty("log.LEVEL", debug ? "DEBUG" : "INFO");
    }
}
