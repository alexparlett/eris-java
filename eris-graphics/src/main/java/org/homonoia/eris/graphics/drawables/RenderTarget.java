package org.homonoia.eris.graphics.drawables;

import org.homonoia.eris.resources.GPUResource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 09/07/2016
 */
public class RenderTarget implements GPUResource {

    private int handle = 0;
    private AtomicInteger width = new AtomicInteger(0);
    private AtomicInteger height = new AtomicInteger(0);

    public RenderTarget width(int width) {
        this.width.set(width);
        return this;
    }

    public RenderTarget height(int height) {
        this.height.set(height);
        return this;
    }

    public int getWidth() {
        return width.get();
    }

    public int getHeight() {
        return height.get();
    }

    @Override
    public void use() {

    }

    @Override
    public int getHandle() {
        return handle;
    }
}
