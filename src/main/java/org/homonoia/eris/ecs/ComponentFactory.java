package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.ecs.exceptions.InvalidComponentException;

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
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvalidComponentException("Cannot create Component of {}", e, clazz.getCanonicalName());
        }
    }


    public <T extends Component> T newInstance(String name) {
        try {
            return (T) Thread.currentThread().getContextClassLoader().loadClass(name).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new InvalidComponentException("Cannot create Component of {}", e, name);
        }
    }
}
