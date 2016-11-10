package org.homonoia.eris.scripting.utils;

import org.homonoia.eris.scripting.ScriptBinding;
import org.springframework.stereotype.Component;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/11/2016
 */
@Component
public class ClassMapper implements ScriptBinding {
    public static Class forName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
}
