package org.homonoia.eris.renderer;

/**
 * Created by alexparlett on 06/02/2016.
 */
public interface RenderState {

    void add(RenderCommand renderCommand);

    void swap();

    void process();

}
