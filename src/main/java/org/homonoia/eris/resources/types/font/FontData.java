package org.homonoia.eris.resources.types.font;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.GPUResource;
import org.homonoia.eris.resources.Resource;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

import static java.util.Objects.nonNull;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alex
 * @since 4/18/17
 */
public class FontData extends Resource {

    private ByteBuffer data;

    public FontData(Context context) {
        super(context);
    }

    @Override
    public void load(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        byte[] buf = new byte[1024];
        while ((read = inputStream.read(buf)) >= 0) {
            baos.write(buf, 0, read);
        }

        if (baos.size() <= 0) {
            throw new IOException(MessageFormat.format("Failed to load Font Data {0}. File empty.", getLocation()));
        }

        data = memAlloc(baos.size());
        data.put(baos.toByteArray());
        data.flip();

        setState(AsyncState.SUCCESS);
    }

    @Override
    public void reset() {
        if (nonNull(data)) {
            memFree(data);
            data = null;
        }
    }

    public ByteBuffer getData() {
        return data;
    }
}
