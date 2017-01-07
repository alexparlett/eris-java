package org.homonoia.eris.ecs;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 10/11/2016
 */
public abstract class ScriptComponent extends Component {
    public Class<? extends Component>[] requires() {
        return new Class[]{};
    }

    public boolean autoAdd() {
        return false;
    }

    public boolean multiple() {
        return false;
    }
}
