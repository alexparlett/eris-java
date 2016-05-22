package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.graphics.GPUResource;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.Image;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 21/02/2016
 */
public abstract class Texture extends Resource implements GPUResource {

    private static final Map<String, Integer> wrapMap = new HashMap<>();

    static {
        wrapMap.put("REPEAT", GL11.GL_REPEAT);
        wrapMap.put("MIRRORED_REPEAT", GL14.GL_MIRRORED_REPEAT);
        wrapMap.put("CLAMP_TO_BORDER", GL12.GL_CLAMP_TO_EDGE);
        wrapMap.put("CLAMP_TO_EDGE", GL13.GL_CLAMP_TO_BORDER);
    }

    protected boolean generateMipMaps = true;
    protected int uWrapMode = GL11.GL_REPEAT;
    protected int vWrapMode = GL11.GL_REPEAT;
    protected int wWrapMode = GL11.GL_REPEAT;
    protected int handle;

    public Texture(final Context context) {
        super(context);
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public void reset() {
        if (handle != MemoryUtil.NULL) {
            glDeleteTextures(handle);
            handle = 0;
        }
    }

    public void setGenerateMipMaps(final boolean generateMipMaps) {
        this.generateMipMaps = generateMipMaps;
    }

    public void setUWrapMode(final int uWrapMode) {
        this.uWrapMode = uWrapMode;
    }

    public void setVWrapMode(final int vWrapMode) {
        this.vWrapMode = vWrapMode;
    }

    public void setWWrapMode(final int wWrapMode) {
        this.wWrapMode = wWrapMode;
    }

    public int getwWrapMode() {
        return wWrapMode;
    }

    public int getvWrapMode() {
        return vWrapMode;
    }

    public int getuWrapMode() {
        return uWrapMode;
    }

    public boolean isGenerateMipMaps() {
        return generateMipMaps;
    }

    protected int getFormat(Image image) {
        switch (image.getComponents()) {
            case 1:
                return GL11.GL_RED;
            case 2:
                return GL30.GL_RG;
            case 3:
                return GL11.GL_RGB;
            case 4:
                return GL11.GL_RGBA;
            default:
                return GL11.GL_RGB;
        }
    }

    protected void parseParameters(final JsonObject element) {
        if (element.has("mipmaps")) {
            generateMipMaps = element.get("mipmaps").getAsBoolean();
        }

        if (element.has("wrap")) {
            JsonObject wrap = element.get("wrap").getAsJsonObject();
            uWrapMode = wrapMap.get(Optional.ofNullable(wrap.getAsJsonPrimitive("u"))
                    .map(JsonPrimitive::getAsString)
                    .orElse("REPEAT"));

            vWrapMode = wrapMap.get(Optional.ofNullable(wrap.getAsJsonPrimitive("v"))
                    .map(JsonPrimitive::getAsString)
                    .orElse("REPEAT"));

            wWrapMode = wrapMap.get(Optional.ofNullable(wrap.getAsJsonPrimitive("u"))
                    .map(JsonPrimitive::getAsString)
                    .orElse("REPEAT"));
        }
    }
}
