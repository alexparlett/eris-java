package org.homonoia.eris.graphics.drawables;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Image;
import org.homonoia.eris.resources.types.Json;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 06/03/2016
 */
public class TextureCube extends Texture {

    private List<Image> faces;

    public TextureCube(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("Failed to load TextureCube. Metadata Json invalid."));

        faces = new ArrayList();

        String right = root.getAsJsonPrimitive("right").getAsString();
        String left = root.getAsJsonPrimitive("left").getAsString();
        String top = root.getAsJsonPrimitive("top").getAsString();
        String bottom = root.getAsJsonPrimitive("bottom").getAsString();
        String front = root.getAsJsonPrimitive("front").getAsString();
        String back = root.getAsJsonPrimitive("back").getAsString();

        faces.add(resourceCache.getTemporary(Image.class, right)
                .orElseThrow(() -> new IOException(MessageFormat.format("Failed to load TextureCube. right face at {0} does not exist.", right))));

        faces.add(resourceCache.getTemporary(Image.class, left)
                .orElseThrow(() -> new IOException(MessageFormat.format("Failed to load TextureCube. left face at {0} does not exist.", left))));

        faces.add(resourceCache.getTemporary(Image.class, top)
                .orElseThrow(() -> new IOException(MessageFormat.format("Failed to load TextureCube. top face at {0} does not exist.", top))));

        faces.add(resourceCache.getTemporary(Image.class, bottom)
                .orElseThrow(() -> new IOException(MessageFormat.format("Failed to load TextureCube. bottom face at {0} does not exist.", bottom))));

        faces.add(resourceCache.getTemporary(Image.class, back)
                .orElseThrow(() -> new IOException(MessageFormat.format("Failed to load TextureCube. back face at {0} does not exist.", back))));

        faces.add(resourceCache.getTemporary(Image.class, front)
                .orElseThrow(() -> new IOException(MessageFormat.format("Failed to load TextureCube. front face at {0} does not exist.", front))));

        parseParameters(root);

        setState(AsyncState.GPU_READY);
    }

    @Override
    public void use() {
        Objects.requireNonNull(handle, "Texture Handle must be set");
        glBindTexture(GL_TEXTURE_CUBE_MAP, handle);
    }

    @Override
    public void compile() throws IOException {
        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, handle);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, generateMipMaps ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, uWrapMode);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, vWrapMode);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, vWrapMode);

        for (int i = 0; i < faces.size(); i++) {
            Image face = faces.get(i);
            int format = getFormat(face);

            glGetError();
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, format, face.getWidth(), face.getHeight(), 0, format, GL_UNSIGNED_BYTE, face.getData());

            int glErrorCode = glGetError();
            if (glErrorCode != GL_NO_ERROR) {
                glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
                glDeleteTextures(handle);
                throw new IOException(MessageFormat.format("Failed to load TextureCube {0}. OpenGL Error {1}", face.getPath(), glErrorCode));
            }
        }

        if (generateMipMaps) {
            glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
        }

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

        setState(AsyncState.SUCCESS);
    }
}
