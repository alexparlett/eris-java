package org.homonoia.eris.scripting;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.scripting.io.ErrWriter;
import org.homonoia.eris.scripting.io.OutWriter;
import org.homonoia.eris.scripting.utils.ClassMapper;
import org.python.core.Py;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

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

    ClassMapper classMapper = new ClassMapper();
    ScriptClassLoader scriptClassLoader = new ScriptClassLoader();
    Map<String, Object> pythonGlobalsTable = new HashMap<>();
    PythonInterpreter pythonInterpreter;

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

    /**
     *
     * @param clazz
     */
    public void bindClass(Class clazz) {
        this.scriptClassLoader.bind(clazz);
    }

    public void initialize() {
        classMapper.bind(this);

        bindings = getContext().getBeans(ScriptBinding.class);
        bindings.forEach(scriptBinding -> scriptBinding.bind(this));

        PySystemState pySystemState = Py.getSystemState();
        pySystemState.setClassLoader(scriptClassLoader);
        pySystemState.setCurrentWorkingDir(FileSystem.getApplicationDataDirectory().toString());

        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDataDirectory().toString()));
        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDirectory().toString()));
        pySystemState.path.append(Py.java2py(FileSystem.getApplicationDirectory().resolve("Data").toString()));

        PyStringMap builtins = (PyStringMap) pySystemState.getBuiltins();
        pythonGlobalsTable.forEach((key,value) -> builtins.getMap().put(key, Py.java2py(value)));

        pythonInterpreter = PythonInterpreter.threadLocalStateInterpreter(null);
        pythonInterpreter.setErr(new ErrWriter());
        pythonInterpreter.setOut(new OutWriter());
    }

    public void shutdown() {
        if (nonNull(pythonInterpreter)) {
            pythonInterpreter.close();
        }
    }

    public PythonInterpreter getPythonInterpreter() {
        return pythonInterpreter;
    }

    public Map<String, Object> getPythonGlobalsTable() {
        return pythonGlobalsTable;
    }

    public ScriptClassLoader getScriptClassLoader() {
        return scriptClassLoader;
    }
}
