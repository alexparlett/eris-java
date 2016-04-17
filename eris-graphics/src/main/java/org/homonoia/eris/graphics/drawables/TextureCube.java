package org.homonoia.eris.graphics.drawables;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Image;
import org.homonoia.eris.resources.types.Json;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 06/03/2016
 */
public class TextureCube extends Texture {

    public TextureCube(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        ResourceCache resourceCache = getContext().getComponent(ResourceCache.class);

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("Failed to load Texture2D. Metadata Json invalid."));

        Map<Integer, Image> faces = new HashMap<>();
        for (JsonElement face : root.getAsJsonArray("faces")) {
            JsonObject asJsonObject = face.getAsJsonObject();

            int pos = parsePosition(asJsonObject.get("pos").getAsString());
            Path file = Paths.get(asJsonObject.get("file").getAsString());
            Path fullPath;
            if (file.getParent() == null) {
                fullPath = getPath().getParent().resolve(file);
            } else {
                fullPath = file;
            }

            Image image = resourceCache.getTemporary(Image.class, fullPath)
                    .orElseThrow(() -> new IOException(MessageFormat.format("Failed to load TextureCube. Face {0} at {1} does not exist.", pos, file)));
            image.flip();

            faces.put(pos,image);
        }

        parseParameters(root);
        compile(faces);
    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void use() {
        Objects.requireNonNull(handle, "Texture Handle must be set");
        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, handle);
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (handle != MemoryUtil.NULL) {
            glDeleteTextures(handle);
            handle = 0;
        }
    }

    private int parsePosition(final String pos) throws IOException {
        switch (pos) {
            case "x":
                return GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
            case "-x":
                return GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
            case "y":
                return GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
            case "-y":
                return GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
            case "z":
                return GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
            case "-z":
                return GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
        }
        throw new IOException(MessageFormat.format("Failed to load TextureCube. Invalid Face Position {0}.", pos));
    }

    private void compile(final Map<Integer, Image> faces) throws IOException {
        long win = GLFW.glfwGetCurrentContext();
        GLFW.glfwMakeContextCurrent(win != MemoryUtil.NULL ? win : getContext().getComponent(Graphics.class).getBackgroundWindow());

        handle = GL11.glGenTextures();
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, handle);

        for (Map.Entry<Integer, Image> face : faces.entrySet())
        {
            int unit = face.getKey();
            int format = getFormat(face.getValue());

            GL11.glGetError();
            GL11.glTexImage2D(unit, 0, format, face.getValue().getWidth(), face.getValue().getHeight(), 0, format, GL_UNSIGNED_BYTE, face.getValue().getData());

            int glErrorCode = GL11.glGetError();
            if (glErrorCode != GL11.GL_NO_ERROR)
            {
                GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
                GL11.glDeleteTextures(handle);
                GLFW.glfwMakeContextCurrent(win);
                throw new IOException(MessageFormat.format("Failed to load TextureCube {0}. OpenGL Error {1}", face.getValue().getPath(), glErrorCode));
            }
        }

        setParameters();

        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        GLFW.glfwMakeContextCurrent(win);
    }
}
