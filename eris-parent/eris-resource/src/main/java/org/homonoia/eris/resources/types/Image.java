package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.image.ImageException;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

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

    public Image(final Context context, final Path location) {
        super(context);
        setLocation(location);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream, "Input Stream must not be null.");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        byte[] buf = new byte[1024];
        while ((read = inputStream.read(buf)) >= 0) {
            baos.write(buf, 0, read);
        }

        if (baos.size() <= 0) {
            throw new IOException(MessageFormat.format("Failed to load Image {0}. File empty.", getLocation()));
        }

        ByteBuffer byteBuffer = memAlloc(baos.size());
        try (MemoryStack stack = stackPush()) {
            byteBuffer.put(baos.toByteArray());
            byteBuffer.flip();

            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(false);
            data = stbi_load_from_memory(byteBuffer, w, h, comp, 0);
            if (isNull(data)) {
                LOG.error("Failed to load Image {}. {}", getLocation(), stbi_failure_reason());
                throw new IOException(MessageFormat.format("Failed to load Image {0}. {1}", getLocation(), stbi_failure_reason()));
            }

            this.width = w.get(0);
            this.height = h.get(0);
            this.components = comp.get(0);
        } finally {
            memFree(byteBuffer);
        }
    }

    @Override
    public void save() throws IOException {
        if (data == null || this.width <= 0 || this.height <= 0 || this.components <= 0) {
            LOG.error("Failed to save Image {}.", getPath());
            throw new IOException(MessageFormatter.format("Failed to save Image {}. Image data invalid.", getPath()).getMessage());
        }

        if (!stbi_write_png(getLocation().toString(), width, height, components, data, width * components)) {
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

        ByteBuffer buffer = memAlloc(width * height * components);
        try {

            if (!stbir_resize_uint8(data, this.width, this.height, this.width * components, buffer, width, height, width * components, components)) {
                throw new ImageException("Failed to resize Image {}. {}.", getPath(), stbi_failure_reason());
            }

            this.width = width;
            this.height = height;
            this.data = buffer;
        } finally {
            memFree(buffer);
        }
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
            stbi_image_free(data);
            data.clear();
            data = null;
            memFree(data);
        }
    }
}
