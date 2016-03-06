package org.homonoia.eris.renderer;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 06/02/2016
 */
public interface RenderState {

    void add(RenderCommand renderCommand);

    void swap();

    void process();

}
