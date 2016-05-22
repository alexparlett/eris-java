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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.MessageFormat;
import java.util.Objects;

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
    public void load(final InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream, "Input Stream must not be null.");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        while ((read = inputStream.read()) >= 0) {
            baos.write(read);
        }

        if (baos.size() <= 0) {
            throw new IOException(MessageFormat.format("Failed to load Image {0}. File empty.", getPath()));
        }

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(baos.size());
        byteBuffer.put(baos.toByteArray());
        byteBuffer.flip();

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        // Use info to read image metadata without decoding the entire image.
        if (stbi_info_from_memory(byteBuffer, w, h, comp) == 0)
            throw new IOException("Failed to read image information: " + stbi_failure_reason());

        stbi_set_flip_vertically_on_load(1);
        data = stbi_load_from_memory(byteBuffer, w, h, comp, 0);
        if (data == null) {
            LOG.error("Failed to load Image {}. {}", getPath(), stbi_failure_reason());
            throw new IOException(MessageFormat.format("Failed to load Image {0}. {1}", getPath(), stbi_failure_reason()));
        }

        this.width = w.get(0);
        this.height = h.get(0);
        this.components = comp.get(0);
    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
        Objects.requireNonNull(outputStream, "Output Stream must not be null.");

        if (data == null || this.width <= 0 || this.height <= 0 || this.components <= 0) {
            LOG.error("Failed to save Image {}.", getPath());
            throw new IOException(MessageFormat.format("Failed to save Image {0}. Image data invalid.", getPath()));
        }

        ByteBuffer writeContext = BufferUtils.createByteBuffer(width * components);
        ByteBuffer outputContext = ByteBuffer.wrap(new byte[width * height * components]);

        int success = STBImageWrite.stbi_write_png_to_func(STBIWriteCallback.create((context, data, size) -> outputContext.put(STBIWriteCallback.getData(data, size))), writeContext, width, height, components, data, width * components);
        if (success == 0) {
            throw new IOException(MessageFormat.format("Failed to save Image {0}.", getPath()));
        }

        outputStream.write(outputContext.array());
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
        if (STBImageResize.stbir_resize_uint8(data, this.width, this.height, this.width * components, buffer, width, height, width * components, components) == 0) {
            throw new ImageException("Failed to resize Image {}.", getPath());
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
