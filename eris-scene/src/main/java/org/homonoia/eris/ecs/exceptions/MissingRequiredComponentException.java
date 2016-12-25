package org.homonoia.eris.ecs.exceptions;

import org.homonoia.eris.core.exceptions.ErisException;
import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.Entity;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/07/2016
 */
public class MissingRequiredComponentException extends ErisException {
    public MissingRequiredComponentException(Class<? extends Component> missingClass, Entity entity, Component addedComponent) {
        super("{1} cannot be added to {2}, {3} is required as a component", addedComponent.getClass().getSimpleName(), entity.getId(), missingClass.getSimpleName());
    }
}
