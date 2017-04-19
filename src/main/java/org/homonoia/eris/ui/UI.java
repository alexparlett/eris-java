package org.homonoia.eris.ui;

import lombok.Getter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.exceptions.ErisRuntimeExcecption;
import org.homonoia.eris.core.exceptions.InitializationException;
import org.homonoia.eris.events.graphics.ScreenMode;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.ShaderProgram;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Font;
import org.homonoia.eris.ui.elements.Panel;
import org.lwjgl.nuklear.NkAllocator;
import org.lwjgl.nuklear.NkBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkConvertConfig;
import org.lwjgl.nuklear.NkDrawCommand;
import org.lwjgl.nuklear.NkDrawNullTexture;
import org.lwjgl.nuklear.NkDrawVertexLayoutElement;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.lwjgl.glfw.GLFW.glfwSetClipboardString;
import static org.lwjgl.glfw.GLFW.nglfwGetClipboardString;
import static org.lwjgl.nuklear.Nuklear.NK_ANTI_ALIASING_ON;
import static org.lwjgl.nuklear.Nuklear.NK_FORMAT_COUNT;
import static org.lwjgl.nuklear.Nuklear.NK_FORMAT_FLOAT;
import static org.lwjgl.nuklear.Nuklear.NK_FORMAT_R8G8B8A8;
import static org.lwjgl.nuklear.Nuklear.NK_VERTEX_ATTRIBUTE_COUNT;
import static org.lwjgl.nuklear.Nuklear.NK_VERTEX_COLOR;
import static org.lwjgl.nuklear.Nuklear.NK_VERTEX_POSITION;
import static org.lwjgl.nuklear.Nuklear.NK_VERTEX_TEXCOORD;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_BACKGROUND;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_ROM;
import static org.lwjgl.nuklear.Nuklear.nk__draw_begin;
import static org.lwjgl.nuklear.Nuklear.nk__draw_next;
import static org.lwjgl.nuklear.Nuklear.nk_buffer_free;
import static org.lwjgl.nuklear.Nuklear.nk_buffer_init;
import static org.lwjgl.nuklear.Nuklear.nk_buffer_init_fixed;
import static org.lwjgl.nuklear.Nuklear.nk_clear;
import static org.lwjgl.nuklear.Nuklear.nk_convert;
import static org.lwjgl.nuklear.Nuklear.nk_free;
import static org.lwjgl.nuklear.Nuklear.nk_init;
import static org.lwjgl.nuklear.Nuklear.nk_style_set_font;
import static org.lwjgl.nuklear.Nuklear.nnk_strlen;
import static org.lwjgl.nuklear.Nuklear.nnk_textedit_paste;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glMapBuffer;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
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

    public static final int NK_WINDOW_NO_INPUT = 1 << 10;
    public static final int NK_WINDOW_NOT_INTERACTIVE = NK_WINDOW_ROM | NK_WINDOW_NO_INPUT;

    private static final int BUFFER_INITIAL_SIZE = 4 * 1024;
    private static final int MAX_VERTEX_BUFFER = 512 * 1024;
    private static final int MAX_ELEMENT_BUFFER = 128 * 1024;

    private final ResourceCache resourceCache;
    private final Graphics graphics;

    @Getter
    private NkContext ctx;
    private NkAllocator allocator;
    private NkBuffer cmds;
    private NkDrawNullTexture nullTexture;

    private Font defaultFont;
    private ShaderProgram shaderProgram;
    private int vbo;
    private int ebo;
    private int vao;

    @Getter
    private List<UIElement> roots = new ArrayList<>();
    private NkDrawVertexLayoutElement.Buffer vertexLayout;


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
        subscribe(this::handleScreenMode, ScreenMode.class);

        allocator = NkAllocator.create();
        allocator.alloc((handle, old, size) -> {
            long mem = nmemAlloc(size);
            if (mem == NULL)
                throw new OutOfMemoryError();

            return mem;

        });
        allocator.mfree((handle, ptr) -> nmemFree(ptr));

        vertexLayout = NkDrawVertexLayoutElement.create(4)
                .position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
                .position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
                .position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
                .position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
                .flip();

        ctx = NkContext.create();
        nk_init(ctx, allocator, null);

        setupClipboard();
        setupContext();
    }

    public void shutdown() {
        if (nonNull(roots)) {
            roots.forEach(UIElement::destroy);
        }

        if (nonNull(nullTexture)) {
            glDeleteTextures(nullTexture.texture().id());
            nullTexture = null;
        }

        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);

        if (nonNull(shaderProgram)) {
            shaderProgram.release();
            shaderProgram = null;
        }

        if (nonNull(defaultFont)) {
            defaultFont.release();
            defaultFont = null;
        }

        if (nonNull(cmds)) {
            nk_buffer_free(cmds);
            cmds = null;
        }

        if (nonNull(ctx)) {
            ctx.clip().copy().free();
            ctx.clip().paste().free();
            nk_free(ctx);
            ctx = null;
        }

        if (nonNull(allocator)) {
            allocator.alloc().free();
            allocator.mfree().free();
            allocator = null;
        }
    }

    private void handleScreenMode(ScreenMode evt) {
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
        nullTexture = NkDrawNullTexture.create();

        // null texture setup
        int nullTexID = glGenTextures();

        nullTexture.texture().id(nullTexID);
        nullTexture.uv().set(0.5f, 0.5f);

        glBindTexture(GL_TEXTURE_2D, nullTexID);
        try (MemoryStack stack = stackPush()) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    }

    private void setupContext() {
        cmds = NkBuffer.create();

        shaderProgram = resourceCache.get(ShaderProgram.class, "Shaders/ui.shader")
                .orElseThrow(() -> new InitializationException("Shaders/ui.shader missing for UI module."));

        defaultFont = resourceCache.get(Font.class, "Fonts/code_18_b.font")
                .orElseThrow(() -> new InitializationException("Fonts/code_18_b.font missing for UI module."));

        nk_buffer_init(cmds, allocator, BUFFER_INITIAL_SIZE);

        setupBuffers();
        setupNullTexture();

        nk_style_set_font(ctx, defaultFont.getNkFont());

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public void render() {
        try ( MemoryStack stack = stackPush() ) {
            // setup global state
            glEnable(GL_BLEND);
            glBlendEquation(GL_FUNC_ADD);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_SCISSOR_TEST);
            glActiveTexture(GL_TEXTURE0);

            // setup program
            shaderProgram.use();
            Uniform texture = shaderProgram.getUniform("Texture")
                    .orElseThrow(() -> new ErisRuntimeExcecption("Shaders/ui.shader missing Texture uniform"));
            glUniform1i(texture.getLocation(), 0);

            Uniform projMtx = shaderProgram.getUniform("ProjMtx")
                    .orElseThrow(() -> new ErisRuntimeExcecption("Shaders/ui.shader missing ProjMtx uniform"));

            glUniformMatrix4fv(projMtx.getLocation(), false, stack.floats(
                    2.0f / graphics.getWidth(), 0.0f, 0.0f, 0.0f,
                    0.0f, -2.0f / graphics.getHeight(), 0.0f, 0.0f,
                    0.0f, 0.0f, -1.0f, 0.0f,
                    -1.0f, 1.0f, 0.0f, 1.0f
            ));
        }

        {
            // convert from command queue into draw list and draw to screen

            // allocate vertex and element buffer
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            glBufferData(GL_ARRAY_BUFFER, MAX_VERTEX_BUFFER, GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_ELEMENT_BUFFER, GL_STREAM_DRAW);

            // load draw vertices & elements directly into vertex + element buffer
            ByteBuffer vertices = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_VERTEX_BUFFER, null);
            ByteBuffer elements = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_ELEMENT_BUFFER, null);
            try ( MemoryStack stack = stackPush() ) {
                // fill convert configuration
                NkConvertConfig config = NkConvertConfig.callocStack(stack)
                        .vertex_layout(vertexLayout)
                        .vertex_size(20)
                        .vertex_alignment(4)
                        .null_texture(nullTexture)
                        .circle_segment_count(22)
                        .curve_segment_count(22)
                        .arc_segment_count(22)
                        .global_alpha(1.0f)
                        .shape_AA(NK_ANTI_ALIASING_ON)
                        .line_AA(NK_ANTI_ALIASING_ON);

                // setup buffers to load vertices and elements
                NkBuffer vbuf = NkBuffer.mallocStack(stack);
                NkBuffer ebuf = NkBuffer.mallocStack(stack);

                nk_buffer_init_fixed(vbuf, vertices/*, max_vertex_buffer*/);
                nk_buffer_init_fixed(ebuf, elements/*, max_element_buffer*/);
                nk_convert(ctx, cmds, vbuf, ebuf, config);
            }
            glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            glUnmapBuffer(GL_ARRAY_BUFFER);

            // iterate over and execute each draw command
            float fb_scale_x = (float) graphics.getDefaultRenderTarget().getWidth() / (float) graphics.getWidth();
            float fb_scale_y = (float) graphics.getDefaultRenderTarget().getHeight() / (float)graphics.getHeight();

            long offset = NULL;
            for (NkDrawCommand cmd = nk__draw_begin(ctx, cmds); cmd != null; cmd = nk__draw_next(cmd, cmds, ctx) ) {
                if ( cmd.elem_count() == 0 ) continue;
                glBindTexture(GL_TEXTURE_2D, cmd.texture().id());
                glScissor(
                        (int)(cmd.clip_rect().x() * fb_scale_x),
                        (int)((graphics.getHeight() - (int)(cmd.clip_rect().y() + cmd.clip_rect().h())) * fb_scale_y),
                        (int)(cmd.clip_rect().w() * fb_scale_x),
                        (int)(cmd.clip_rect().h() * fb_scale_y)
                );
                glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset);
                offset += cmd.elem_count() * 2;
            }
            nk_clear(ctx);
        }

        // default OpenGL state
        glUseProgram(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        glDisable(GL_BLEND);
        glDisable(GL_SCISSOR_TEST);
    }
}