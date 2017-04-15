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
import org.lwjgl.nuklear.NkDrawNullTexture;
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
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
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

    @Getter
    private NkContext ctx = NkContext.create();
    private NkAllocator allocator = NkAllocator.create();
    private NkBuffer cmds = NkBuffer.create();
    private NkDrawNullTexture nullTexture = NkDrawNullTexture.create();

    private ShaderProgram shaderProgram;
    private int vbo;
    private int ebo;
    private int vao;


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

        if (nonNull(nullTexture)) {
            glDeleteTextures(nullTexture.texture().id());
        }

        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);

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


    private void setupBuffers() {
        int positionAttribute = shaderProgram.getAttribute("Position")
                .orElseThrow(() -> new InitializationException("Position Attribute missing in UI shader."))
                .getLocation();

        int uvAttribute = shaderProgram.getAttribute("TexCoord")
                .orElseThrow(() -> new InitializationException("TexCoord Attribute missing in UI shader."))
                .getLocation();

        int colorAttribute = shaderProgram.getAttribute("Color")
                .orElseThrow(() -> new InitializationException("Color Attribute missing in UI shader."))
                .getLocation();

        vbo = glGenBuffers();
        ebo = glGenBuffers();
        vao = glGenVertexArrays();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glEnableVertexAttribArray(positionAttribute);
        glEnableVertexAttribArray(uvAttribute);
        glEnableVertexAttribArray(colorAttribute);

        glVertexAttribPointer(positionAttribute, 2, GL_FLOAT, false, 20, 0);
        glVertexAttribPointer(uvAttribute, 2, GL_FLOAT, false, 20, 8);
        glVertexAttribPointer(colorAttribute, 4, GL_UNSIGNED_BYTE, true, 20, 16);
    }

    private void setupNullTexture() {
        // null texture setup
        int nullTexID = glGenTextures();

        nullTexture.texture().id(nullTexID);
        nullTexture.uv().set(0.5f, 0.5f);

        glBindTexture(GL_TEXTURE_2D, nullTexID);
        try ( MemoryStack stack = stackPush() ) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    }

    private void setupContext() {
        shaderProgram = resourceCache.get(ShaderProgram.class, "Shaders/ui.shader")
                .orElseThrow(() -> new InitializationException("Shaders/ui.shader missing for UI module."));

        nk_buffer_init(cmds, allocator, BUFFER_INITIAL_SIZE);

        setupBuffers();
        setupNullTexture();

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
}