package org.homonoia.eris.resources;

import java.io.IOException;

/**
 * Created by alexparlett on 17/04/2016.
 */
public interface GPUResource {
    void use();
    int getHandle();
    default void compile() throws IOException {}
}
