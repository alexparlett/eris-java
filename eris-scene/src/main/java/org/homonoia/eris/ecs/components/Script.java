package org.homonoia.eris.ecs.components;

import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.annotations.Multiple;
import org.homonoia.eris.resources.types.Python;
import org.python.util.PythonInterpreter;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/11/2016
 */
@Multiple
public class Script extends Component {

    private final PythonInterpreter interpreter;
    private final Python pythonFile;

    public Script(Python pythonFile) {
        this.pythonFile = pythonFile;

        this.interpreter = PythonInterpreter.threadLocalStateInterpreter(null);
        this.interpreter.set("__file__", pythonFile.getPath().toString());
    }

    public void execute() {
        interpreter.exec(pythonFile.getContents());
    }

    public void bind(String key, Object value) {
        interpreter.set(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Script script = (Script) o;

        return pythonFile != null ? pythonFile.equals(script.pythonFile) : script.pythonFile == null;

    }

    @Override
    public int hashCode() {
        return pythonFile != null ? pythonFile.hashCode() : 0;
    }
}
