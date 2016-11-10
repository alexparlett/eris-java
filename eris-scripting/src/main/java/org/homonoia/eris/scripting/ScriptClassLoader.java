package org.homonoia.eris.scripting;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 08/11/2016
 */
public class ScriptClassLoader extends ClassLoader {

    private Set<String> allowedClasses = new HashSet<>();

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        String className = allowedClasses.stream()
                .filter(allowedClass -> allowedClass.equals(name))
                .findFirst()
                .orElseThrow(() -> new ClassNotFoundException("class loading restricted. " + name + " not allowed."));
        return super.findClass(className);
    }

    protected void bind(Class clazz) {
        this.allowedClasses.add(clazz.getName());
    }
}
