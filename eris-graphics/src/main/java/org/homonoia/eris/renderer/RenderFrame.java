package org.homonoia.eris.renderer;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public interface RenderFrame<T extends RenderFrame> {

    /**
     * Add.
     *
     * @param renderCommand the render command
     */
    T add(final RenderCommand renderCommand);

    /**
     * Sort.
     */
    T sort();

    /**
     * Process.
     */
    T process();

    T clear();
}
