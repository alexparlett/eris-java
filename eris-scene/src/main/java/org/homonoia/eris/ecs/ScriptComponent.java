package org.homonoia.eris.ecs;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 10/11/2016
 */
public interface ScriptComponent extends Component {

    default Class<? extends Component>[] classes() { return new Class[]{}; }

    default boolean autoAdd() { return false; }

    default boolean multiple() { return false; }
}
