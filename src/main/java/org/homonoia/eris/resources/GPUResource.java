package org.homonoia.eris.resources;

import java.io.IOException;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 17/04/2016
 */
public interface GPUResource {
    void use();
    int getHandle();
    default void compile() throws IOException {}
}
