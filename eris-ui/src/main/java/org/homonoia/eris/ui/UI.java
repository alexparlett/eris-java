package org.homonoia.eris.ui;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.scripting.ScriptBinding;
import org.homonoia.eris.scripting.ScriptEngine;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 25/12/2016
 */
public class UI extends Contextual implements ScriptBinding {

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public UI(Context context) {
        super(context);
        context.registerBean(this);
    }

    public void initialize() throws InitializationException {
    }

    @Override
    public void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindGlobal("ui", this);
    }
}
