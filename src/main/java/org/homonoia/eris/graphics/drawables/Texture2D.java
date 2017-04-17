package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Image;
import org.homonoia.eris.resources.types.Json;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 21/02/2016
 */
public class Texture2D extends Texture {

    private Image image;

    public Texture2D(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("Failed to load Texture2D. Metadata Json invalid."));
        parseParameters(root);

        Path file = Paths.get(root.get("file").getAsString());

        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);
        image = resourceCache.getTemporary(Image.class, file)
                .orElseThrow(() -> new IOException("Failed to load Texture2D. Metadata Json doesn't contain valid file: " + file));

        setState(AsyncState.GPU_READY);
    }

    @Override
    public void use() {
        Objects.requireNonNull(handle, "Texture Handle must be set");
        glBindTexture(GL_TEXTURE_2D, handle);
    }

    @Override
    public void compile() throws IOException {
        int format = getFormat(image);

        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, generateMipMaps ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, uWrapMode);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, vWrapMode);

        glGetError();
        glTexImage2D(GL_TEXTURE_2D, 0, format, image.getWidth(), image.getHeight(), 0, format, GL_UNSIGNED_BYTE, image.getData());

        int glErrorCode = glGetError();
        if (glErrorCode != GL_NO_ERROR) {
            glBindTexture(GL_TEXTURE_2D, 0);
            glDeleteTextures(handle);
            throw new IOException(MessageFormat.format("Failed to load TextureCube {0}. OpenGL Error {1}", image.getPath(), glErrorCode));
        }

        if (generateMipMaps) {
            glGenerateMipmap(GL_TEXTURE_2D);
        }

        glBindTexture(GL_TEXTURE_2D, 0);

        setState(AsyncState.SUCCESS);
    }
}
