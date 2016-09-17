package org.homonoia.eris.ecs.components;

import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.annotations.Requires;
import org.homonoia.eris.graphics.drawables.Model;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 19/07/2016
 */
@Requires(classes = {Transform.class})
public class Mesh implements Component {

    private Model model;

    public Model getModel() {
        return model;
    }

    public Mesh model(Model model) {
        this.model = model;
        return this;
    }
}
