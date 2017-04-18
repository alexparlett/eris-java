package org.homonoia.eris.ui;

import lombok.Getter;
import lombok.Setter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 18/04/2017
 */
@Getter
@Setter
public abstract class UIElement extends Contextual {

    protected UI ui;
    protected boolean active = true;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public UIElement(Context context) {
        super(context);
        this.ui = context.getBean(UI.class);
    }

    public abstract void layout();
}
