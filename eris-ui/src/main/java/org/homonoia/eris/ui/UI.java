package org.homonoia.eris.ui;

import lombok.Getter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.ShaderProgram;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.lwjgl.nuklear.NkAllocator;
import org.lwjgl.nuklear.NkBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

import static java.util.Objects.nonNull;
import static org.lwjgl.glfw.GLFW.glfwSetClipboardString;
import static org.lwjgl.glfw.GLFW.nglfwGetClipboardString;
import static org.lwjgl.nuklear.Nuklear.nk_buffer_free;
import static org.lwjgl.nuklear.Nuklear.nk_buffer_init;
import static org.lwjgl.nuklear.Nuklear.nk_free;
import static org.lwjgl.nuklear.Nuklear.nk_init;
import static org.lwjgl.nuklear.Nuklear.nnk_strlen;
import static org.lwjgl.nuklear.Nuklear.nnk_textedit_paste;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.nmemAlloc;
import static org.lwjgl.system.MemoryUtil.nmemFree;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 25/12/2016
 */
public class UI extends Contextual {

    private static final int BUFFER_INITIAL_SIZE = 4 * 1024;
    private static final int MAX_VERTEX_BUFFER = 512 * 1024;
    private static final int MAX_ELEMENT_BUFFER = 128 * 1024;

    private final ResourceCache resourceCache;
    private final Graphics graphics;

    private NkAllocator allocator = NkAllocator.create();

    @Getter
    private NkContext ctx = NkContext.create();

    private NkBuffer cmds = NkBuffer.create();
    private ShaderProgram shaderProgram;


    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public UI(Context context, ResourceCache resourceCache, Graphics graphics) {
        super(context);
        this.resourceCache = resourceCache;
        this.graphics = graphics;
        context.registerBean(this);
    }

    public void initialize() throws InitializationException {
        allocator.alloc((handle, old, size) -> {
            long mem = nmemAlloc(size);
            if (mem == NULL)
                throw new OutOfMemoryError();

            return mem;

        });
        allocator.mfree((handle, ptr) -> nmemFree(ptr));

        nk_init(ctx, allocator, null);

        setupClipboard();
        setupContext();
    }

    public void shutdown() {
        if (nonNull(ctx)) {
            ctx.clip().copy().free();
            ctx.clip().paste().free();
            nk_free(ctx);
        }

        if (nonNull(shaderProgram)) {
            shaderProgram.release();
        }

        if (nonNull(cmds)) {
            nk_buffer_free(cmds);
        }

        if (nonNull(allocator)) {
            allocator.alloc().free();
            allocator.mfree().free();
        }
    }

    private void setupClipboard() {
        ctx.clip().copy((handle, text, len) -> {
            if (len == 0)
                return;

            try (MemoryStack stack = stackPush()) {
                ByteBuffer str = stack.malloc(len + 1);
                memCopy(text, memAddress(str), len);
                str.put(len, (byte) 0);

                glfwSetClipboardString(graphics.getRenderWindow(), str);
            }
        });

        ctx.clip().paste((handle, edit) -> {
            long text = nglfwGetClipboardString(graphics.getRenderWindow());
            if (text != NULL)
                nnk_textedit_paste(edit, text, nnk_strlen(text));
        });
    }


    private void setupContext() {
        shaderProgram = resourceCache.get(ShaderProgram.class, "Shaders/ui.shader")
                .orElseThrow(() -> new InitializationException("Shaders/ui.shader missing for UI module."));

        nk_buffer_init(cmds, allocator, BUFFER_INITIAL_SIZE);

    }
}