package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.scripting.ScriptEngine;
import org.python.core.PyCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/11/2016
 */
public class Python extends Resource {

    private PyCode contents;

    public Python(Context context) {
        super(context);
    }

    @Override
    public void load(InputStream inputStream) throws IOException {
        ScriptEngine scriptEngine = getContext().getBean(ScriptEngine.class);
        contents = scriptEngine.getPythonInterpreter().compile(new InputStreamReader(inputStream), getPath().toString());
    }

    public PyCode getContents() {
        return contents;
    }
}
