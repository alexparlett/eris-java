package org.homonoia.eris.ecs.components;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.annotations.Multiple;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 18/04/2017
 */
@Multiple
public abstract class UIElement extends Component {
    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public UIElement(Context context) {
        super(context);
    }
}
