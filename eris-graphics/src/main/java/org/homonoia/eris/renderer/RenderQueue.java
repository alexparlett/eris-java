package org.homonoia.eris.renderer;

/**
 * Created by alexparlett on 06/02/2016.
 */
public interface RenderQueue {

    /**
     * Add.
     *
     * @param renderCommand the render command
     */
    void add(final RenderCommand renderCommand);

    /**
     * Sort.
     */
    void sort();

    /**
     * Process.
     */
    void process();

    /**
     * Clear.
     */
    void clear();

}
