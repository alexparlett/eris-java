package org.homonoia.eris.ecs.components;

import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.resources.types.Python;
import org.homonoia.eris.scripting.ScriptEngine;
import org.python.util.PythonInterpreter;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/11/2016
 */
public class Script implements Component {

    private final ScriptEngine scriptEngine;
    private final PythonInterpreter interpreter;
    private final Python pythonFile;

    public Script(ScriptEngine scriptEngine, Python pythonFile) {
        this.scriptEngine = scriptEngine;
        this.pythonFile = pythonFile;
        this.interpreter = new PythonInterpreter(null, scriptEngine.getPySystemState());
    }

    public void execute() {
        interpreter.exec(pythonFile.getContents());
    }

    public void bind(String key, Object value) {
        interpreter.set(key, value);
    }
}
