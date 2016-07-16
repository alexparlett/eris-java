package org.homonoia.eris.graphics.drawables;

import org.homonoia.eris.graphics.GPUResource;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/07/2016
 */
public class RenderTarget implements GPUResource {

    private int handle;

    @Override
    public void use() {

    }

    @Override
    public int getHandle() {
        return handle;
    }
}
