package org.homonoia.eris.resources.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.GPUResource;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.font.FontData;
import org.lwjgl.nuklear.NkUserFont;
import org.lwjgl.nuklear.NkUserFontGlyph;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.MessageFormat;

import static java.util.Objects.nonNull;
import static org.lwjgl.nuklear.Nuklear.NK_UTF_INVALID;
import static org.lwjgl.nuklear.Nuklear.nnk_utf_decode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alex
 * @since 4/18/17
 */
public class Font extends Resource implements GPUResource {

    private static final int BITMAP_W = 1024;
    private static final int BITMAP_H = 1024;

    private FontData fontData;
    private ByteBuffer data;
    private int size;
    private float descent;
    private float scale;
    private int handle;
    private STBTTPackedchar.Buffer cdata;
    private STBTTFontinfo fontInfo;
    @Getter
    private NkUserFont nkFont;

    public Font(Context context) {
        super(context);
    }

    @Override
    public void load(InputStream inputStream) throws IOException {
        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonElement root = json.getRoot().orElseThrow(() -> new IOException(MessageFormat.format("No Root Element Found in {0}", getPath())));
        JsonObject asJsonObject = root.getAsJsonObject();
        String font = asJsonObject.getAsJsonPrimitive("font").getAsString();
        size = asJsonObject.getAsJsonPrimitive("size").getAsInt();
        fontData = resourceCache.get(FontData.class, font).orElseThrow(() -> new IOException(MessageFormat.format("Font Data {0} not found for Font {1}", getPath(), font)));

        fontInfo = STBTTFontinfo.create();
        cdata = STBTTPackedchar.create(95);

        try (MemoryStack stack = stackPush()) {
            stbtt_InitFont(fontInfo, fontData.getData());
            scale = stbtt_ScaleForPixelHeight(fontInfo, size);

            IntBuffer d = stack.mallocInt(1);
            stbtt_GetFontVMetrics(fontInfo, null, d, null);
            descent = d.get(0) * scale;

            ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

            STBTTPackContext pc = STBTTPackContext.mallocStack(stack);
            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
            stbtt_PackSetOversampling(pc, 4, 4);
            stbtt_PackFontRange(pc, fontData.getData(), 0, size, 32, cdata);
            stbtt_PackEnd(pc);

            // Convert R8 to RGBA8
            data = memAlloc(BITMAP_W * BITMAP_H * 4);
            for (int i = 0; i < bitmap.capacity(); i++) {
                data.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
            }
            data.flip();

            memFree(bitmap);
        }

        setState(AsyncState.GPU_READY);
    }

    @Override
    public void compile() throws IOException {
        handle = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, handle);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, data);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        nkFont = NkUserFont.create();

        nkFont.width((handle, h, text, len) -> {
            float textWidth = 0;
            try (MemoryStack stack = stackPush()) {
                IntBuffer unicode = stack.mallocInt(1);

                int glyphLen = nnk_utf_decode(text, memAddress(unicode), len);
                int textLen = glyphLen;

                if (glyphLen == 0)
                    return 0;

                IntBuffer advance = stack.mallocInt(1);
                while (textLen <= len && glyphLen != 0) {
                    if (unicode.get(0) == NK_UTF_INVALID) {
                        break;
                    }

                    /* query currently drawn glyph information */
                    stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
                    textWidth += advance.get(0) * scale;

                    /* offset next glyph */
                    glyphLen = nnk_utf_decode(text + textLen, memAddress(unicode), len - textLen);
                    textLen += glyphLen;
                }
            }
            return textWidth;
        });

        nkFont.height(size);

        nkFont.query((handle, font_height, glyph, codepoint, nextCodepoint) -> {
            try (MemoryStack stack = stackPush()) {
                FloatBuffer x = stack.floats(0.0f);
                FloatBuffer y = stack.floats(0.0f);

                STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
                IntBuffer advance = stack.mallocInt(1);

                stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, q, false);
                stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

                NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

                ufg.width(q.x1() - q.x0());
                ufg.height(q.y1() - q.y0());
                ufg.offset().set(q.x0(), q.y0() + (size + descent));
                ufg.xadvance(advance.get(0) * scale);
                ufg.uv(0).set(q.s0(), q.t0());
                ufg.uv(1).set(q.s1(), q.t1());
            }
        });
        nkFont.texture().id(handle);

        glBindTexture(GL_TEXTURE_2D, 0);

        setState(AsyncState.SUCCESS);
    }

    @Override
    public void use() {

    }

    @Override
    public void reset() {
        if (nonNull(fontData)) {
            fontData.release();
            fontData = null;
        }

        if (nonNull(data)) {
            memFree(data);
            data = null;
        }

        if (nonNull(nkFont)) {
            nkFont.query().free();
            nkFont.width().free();
            nkFont = null;
        }

        if (handle != MemoryUtil.NULL) {
            glDeleteTextures(handle);
            handle = 0;
        }

        if (nonNull(cdata)) {
            cdata = null;
        }

        if (nonNull(fontInfo)) {
            fontInfo = null;
        }
    }

    @Override
    public int getHandle() {
        return handle;
    }
}
