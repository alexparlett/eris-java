package org.homonoia.eris.scripting;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.components.FileSystem;
import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 31/07/2016
 */
public class ScriptEngine extends Contextual {

    ScriptClassLoader scriptClassLoader = new ScriptClassLoader();
    PyDictionary pythonGlobalsTable = new PyDictionary();
    PySystemState pySystemState;
    PythonInterpreter pythonInterpreter;

    @Autowired
    private List<ScriptBinding> bindings;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public ScriptEngine(Context context) {
        super(context);
    }

    public void bindGlobal(String name, Object global) {
        bindClass(global.getClass());
        pythonGlobalsTable.put(name, global);
    }

    public void bindClass(Class clazz) {
        this.scriptClassLoader.bind(clazz);
    }

    public void initialize() {
        bindings.forEach(scriptBinding -> scriptBinding.bind(this));

        pySystemState = new PySystemState();
        pySystemState.setClassLoader(scriptClassLoader);
        pySystemState.setCurrentWorkingDir(FileSystem.getApplicationDataDirectory().toString());

        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDataDirectory().toString()));
        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDirectory().toString()));
        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDirectory().resolve("Data").toString()));

        pythonInterpreter = new PythonInterpreter(pythonGlobalsTable, pySystemState);
    }

    public void shutdown() {
        if (nonNull(pySystemState)) {
            pySystemState.close();
        }
    }

    public PyDictionary getPythonGlobalsTable() {
        return pythonGlobalsTable;
    }

    public PySystemState getPySystemState() {
        return pySystemState;
    }

    public PythonInterpreter getPythonInterpreter() {
        return pythonInterpreter;
    }
}
