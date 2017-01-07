package org.homonoia.eris.scripting;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 08/11/2016
 */
public interface ScriptBinding {

    default void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindClass(this.getClass());
    }

}
