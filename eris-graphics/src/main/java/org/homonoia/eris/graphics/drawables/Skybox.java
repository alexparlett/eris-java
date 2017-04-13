package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.exceptions.ParseException;
import org.homonoia.eris.graphics.GPUResource;
import org.homonoia.eris.graphics.drawables.model.GenerationState;
import org.homonoia.eris.graphics.drawables.primitives.Cube;
import org.homonoia.eris.graphics.drawables.primitives.factory.CubeFactory;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_DEPTH_FUNC;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glGetInteger;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/12/2016
 */
public class Skybox extends Resource implements GPUResource {

    private GenerationState generationState = GenerationState.LOADER;
    private Material material;
    private Cube cube;

    public Skybox(Context context) {
        super(context);
    }

    @Override
    public int getHandle() {
        return cube.getHandle();
    }

    @Override
    public void load(InputStream inputStream) throws IOException {
        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);
        CubeFactory cubeFactory = getContext().getBean(CubeFactory.class);

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("Failed to load Skybox. Metadata Json invalid."));

        material = Optional.ofNullable(root.getAsJsonPrimitive("material"))
                .map(JsonPrimitive::getAsString)
                .map(Paths::get)
                .map(file -> resourceCache.get(Material.class, file))
                .orElseThrow(() -> new ParseException("material specified for {0} not found", getPath()))
                .orElseThrow(() -> new ParseException("material is required for skybox, e.g. 'material': 'Materials/skybox.mat'"));

        cube = cubeFactory.getObject();
    }

    @Override
    public void use() {
        int originalDepthFunction = glGetInteger(GL_DEPTH_FUNC);
        glDepthFunc(GL_LEQUAL);
        cube.use();
        glDepthFunc(originalDepthFunction);
    }

    @Override
    public void reset() {
        material.release();
    }

    public Material getMaterial() {
        return material;
    }
}
