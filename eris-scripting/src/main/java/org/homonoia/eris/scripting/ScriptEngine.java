package org.homonoia.eris.scripting;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.MathLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.LuajavaLib;
import org.luaj.vm2.luajc.LuaJC;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 31/07/2016
 */
public class ScriptEngine extends Contextual {

    private static Globals globals = new Globals();
    private Set<LuaValue> libraries = new HashSet<>();

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public ScriptEngine(Context context) {
        super(context);

        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new StringLib());
        globals.load(new MathLib());
        globals.load(new LuajavaLib());

        LuaJC.install(globals);
    }

    public static <T extends LuaValue> void bind(T library) {
        globals.add(library);
    }
}
