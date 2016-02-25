package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
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

/**
 * Created by alexparlett on 21/02/2016.
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read = inputStream.read();
        while(read >= 0) {
            baos.write(read);
            read = inputStream.read();
        }

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(baos.size());
        byteBuffer.put(baos.toByteArray());

        IntBuffer status = BufferUtils.createIntBuffer(3);

        STBImage.stbi_set_flip_vertically_on_load(1);
        data = STBImage.stbi_load_from_memory(byteBuffer, status, status, status, 0);
        width = status.get(0);
        height = status.get(1);
        components = status.get(2);
        if (data == null || width <= 0 || height <= 0 || components <= 0) {
            LOG.error("Failed to load Image {}. {}", getPath(), STBImage.stbi_failure_reason());
            throw new IOException(MessageFormat.format("Failed to load Image {0}. {1}", getPath(), STBImage.stbi_failure_reason()));
        }
    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
        ByteBuffer writeContext = BufferUtils.createByteBuffer(width * components);
        ByteBuffer outputContext = BufferUtils.createByteBuffer(width * height * components);



        int success = STBImageWrite.stbi_write_png_to_func(STBIWriteCallback.create((context, data, size) -> outputContext.put(STBIWriteCallback.getData(data, size))), writeContext, width, height, components, data, width * components);
        if (success == 0) {
            throw new IOException(MessageFormat.format("Failed to save Image {0}.", getPath()));
        }

        outputStream.write(outputContext.array());
    }

    public boolean resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return false;
        }

        if (width == 0 || height == 0) {
            return false;
        }

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * components);
        if (STBImageResize.stbir_resize_uint8(data, this.width, this.height, this.width * components, buffer, width, height, width * components, components) == 0) {
            LOG.error("Failed to resize Image {}.", getPath());
            return false;
        }

        this.width = width;
        this.height = height;
        this.data = buffer;

        return true;
    }

    public void flip() {
        int rowSize = components * width;
        ByteBuffer rowBuffer = BufferUtils.createByteBuffer(rowSize);
        ByteBuffer oppositeRowBuffer = BufferUtils.createByteBuffer(rowSize);
        int halfRows = height / 2;

        for (int i = 0; i < halfRows; i++)
        {
            System.arraycopy(data.array(), getPixelOffset(0, i), rowBuffer.array(), 0, rowSize);
            System.arraycopy(data.array(), getPixelOffset(0, height - i - 1), oppositeRowBuffer.array(), 0, rowSize);

            System.arraycopy(oppositeRowBuffer.array(), 0, data.array(), getPixelOffset(0, i), rowSize);
            System.arraycopy(rowBuffer.array(), 0, data.array(), getPixelOffset(0, height - i - 1), rowSize);
        }
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

    public int getPixelOffset(int column, int row)
    {
        return (row * width + column) * components;
    }
}
