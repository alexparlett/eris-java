package org.homonoia.eris.scripting;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.components.FileSystem;
import org.python.core.Py;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 31/07/2016
 */
public class ScriptEngine extends Contextual {

    public static final String GLOBAL_PREFIX = "_";
    ScriptClassLoader scriptClassLoader = new ScriptClassLoader();
    Map<String, Object> pythonGlobalsTable = new HashMap<>();
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
        if (!name.startsWith(GLOBAL_PREFIX)) {
            name = GLOBAL_PREFIX + name;
        }
        bindClass(global.getClass());
        pythonGlobalsTable.put(name, global);
    }

    public void bindClass(Class clazz) {
        this.scriptClassLoader.bind(clazz);
    }

    public void initialize() {
        bindings.forEach(scriptBinding -> scriptBinding.bind(this));

        pySystemState = Py.getSystemState();
        pySystemState.setClassLoader(scriptClassLoader);
        pySystemState.setCurrentWorkingDir(FileSystem.getApplicationDataDirectory().toString());

        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDataDirectory().toString()));
        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDirectory().toString()));
        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDirectory().resolve("Data").toString()));

        PyStringMap builtins = (PyStringMap) pySystemState.getBuiltins();
        pythonGlobalsTable.forEach((key,value) -> builtins.getMap().put(key, Py.java2py(value)));

//        pySystemState.stdout = Py.java2py(new OutWriter());
//        pySystemState.stderr = Py.java2py(new ErrWriter());
//
        pythonInterpreter = new PythonInterpreter(null, pySystemState);
    }

    public void shutdown() {
        if (nonNull(pySystemState)) {
            pySystemState.close();
        }
    }

    public PySystemState getPySystemState() {
        return pySystemState;
    }

    public PythonInterpreter getPythonInterpreter() {
        return pythonInterpreter;
    }
}
