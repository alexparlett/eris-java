package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.ecs.exceptions.InvalidComponentException;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 14/04/2017
 */
public class ComponentFactory extends Contextual {

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public ComponentFactory(Context context) {
        super(context);
        context.registerBean(this);
    }

    public <T extends Component> T newInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor(Context.class).newInstance(getContext());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new InvalidComponentException("Cannot create Component of {}", e, clazz.getCanonicalName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T newInstance(String name) {
        try {
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(name);
            return (T) aClass.getConstructor(Context.class).newInstance(getContext());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            throw new InvalidComponentException("Cannot create Component of {}", e, name);
        }
    }
}
