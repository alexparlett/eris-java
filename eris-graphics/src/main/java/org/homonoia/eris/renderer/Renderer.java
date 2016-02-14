package org.homonoia.eris.renderer;

import org.joml.Matrix4f;

/**
 * Created by alexparlett on 06/02/2016.
 */
public interface Renderer {

    void initialize();
    void terminate();

    void setCurrentView(final Matrix4f view);
    void setCurrentPerspective(final Matrix4f perspective);

    Matrix4f getCurrentView();
    Matrix4f getCurrentPerspective();

    void bindUniform(final int location, final int type, final Object data);

}
