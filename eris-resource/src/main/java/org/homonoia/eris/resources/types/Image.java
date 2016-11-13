package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.image.ImageException;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.MessageFormat;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.lwjgl.stb.STBImage.*;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 21/02/2016
 */
public class Image extends Resource {

    private static final Logger LOG = LoggerFactory.getLogger(Image.class);

    private int components;
    private int width;
    private int height;
    private ByteBuffer data;

    public Image(final Context context) {
        super(context);
    }

    @Override
    public void onLoad() throws IOException {
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);
        data = stbi_load(getLocation().toString(), w, h, comp, 0);
        if (isNull(data)) {
            LOG.error("Failed to load Image {}. {}", getLocation(), stbi_failure_reason());
            throw new IOException(MessageFormat.format("Failed to load Image {0}. {1}", getLocation(), stbi_failure_reason()));
        }

        this.width = w.get(0);
        this.height = h.get(0);
        this.components = comp.get(0);
    }

    @Override
    public void onSave() throws IOException {
        if (data == null || this.width <= 0 || this.height <= 0 || this.components <= 0) {
            throw new IOException(MessageFormatter.format("Failed to save Image {}. Image data invalid.", getLocation()).getMessage());
        }

        if (!STBImageWrite.stbi_write_png(getLocation().toString(), getWidth(), getHeight(), getComponents(), getData(), getWidth() * getComponents())) {
            throw new IOException(MessageFormatter.format("Failed to save Image {}. {}", getPath(), stbi_failure_reason()).getMessage());
        }
    }

    public void resize(int width, int height) throws ImageException {
        if (this.width == width && this.height == height) {
            return;
        }

        if (width == 0 || height == 0) {
            throw new ImageException("Failed to resize {}. Width and Height must be greater than 0.", getPath());
        }

        if (data == null) {
            throw new ImageException("Failed to resize {}. No Image Data.", getPath());
        }

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * components);

        if (STBImageResize.stbir_resize_uint8(data, this.width, this.height, this.width * components, buffer, width, height, width * components, components)) {
            throw new ImageException("Failed to resize Image {}. {}.", getPath(), stbi_failure_reason());
        }

        this.width = width;
        this.height = height;
        this.data = buffer;
    }

    public void flip() {
        data.flip();
    }

    public int getComponents() {
        return components;
    }

    public void setComponents(final int components) {
        this.components = components;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public ByteBuffer getData() {
        return data;
    }

    public void setData(final ByteBuffer data) {
        this.data = data;
    }

    public int getPixelOffset(int column, int row) {
        return (row * width + column) * components;
    }

    @Override
    public void reset() {
        if (data != null) {
            STBImage.stbi_image_free(data);
            data.clear();
            data = null;
        }
    }
}
