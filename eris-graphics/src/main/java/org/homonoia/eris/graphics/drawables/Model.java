package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.homonoia.eris.core.Constants;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.exceptions.ParseException;
import org.homonoia.eris.core.parsers.Vector3fParser;
import org.homonoia.eris.graphics.GPUResource;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.model.AxisAlignedBoundingBox;
import org.homonoia.eris.graphics.drawables.model.SubModel;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.homonoia.eris.resources.types.Mesh;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class Model extends Resource implements GPUResource {

    private int handle = UUID.randomUUID().hashCode();
    private List<SubModel> subModels = new ArrayList<>();
    private Renderer renderer;
    private AxisAlignedBoundingBox axisAlignedBoundingBox;

    public Model(final Context context) {
        super(context);
        renderer = context.getBean(Renderer.class);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        Json json = new Json(getContext());
        json.load(inputStream);

        JsonArray root = json.getRoot().map(JsonElement::getAsJsonArray)
                .orElseThrow(() -> new IOException("no root found"));

        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);

        try {
            root.forEach(jsonElement -> {
                JsonObject subModelJson = jsonElement.getAsJsonObject();

                Material material = Optional.ofNullable(subModelJson.getAsJsonPrimitive("material"))
                        .map(JsonPrimitive::getAsString)
                        .map(Paths::get)
                        .map(file -> resourceCache.get(Material.class, file))
                        .orElseThrow(() -> new ParseException("material specified for {0} not found", getPath()))
                        .orElseThrow(() -> new ParseException("material is required for models, e.g. 'material': 'Materials/planet.mat'"));

                float scale = Optional.ofNullable(subModelJson.getAsJsonPrimitive("scale"))
                        .map(JsonPrimitive::getAsFloat)
                        .orElse(1.f);

                Vector3f origin = Optional.ofNullable(subModelJson.getAsJsonPrimitive("origin"))
                        .map(JsonPrimitive::getAsString)
                        .map(string -> {
                            try {
                                return Vector3fParser.parse(string);
                            } catch (java.text.ParseException e) {
                                throw new ParseException(e);
                            }
                        })
                        .orElse(Constants.VectorConstants.ZERO);

                Mesh mesh = Optional.ofNullable(subModelJson.getAsJsonPrimitive("mesh"))
                        .map(JsonPrimitive::getAsString)
                        .map(Paths::get)
                        .map(file -> resourceCache.get(Mesh.class, file))
                        .orElseThrow(() -> new ParseException("mesh specified for {0} not found", getPath()))
                        .orElseThrow(() -> new ParseException("mesh is required for models, e.g. 'mesh': 'Meshes/sphere.obj'"));

                subModels.add(SubModel.builder()
                        .material(material)
                        .mesh(mesh)
                        .scale(scale)
                        .origin(origin)
                        .build());
            });
        } catch (ParseException | IllegalStateException ex) {
            reset();
            throw new IOException(ex);
        }

        Graphics graphics = getContext().getBean(Graphics.class);
        subModels.forEach(subModel -> subModel.compile(graphics));

        axisAlignedBoundingBox = AxisAlignedBoundingBox.generate(this);
    }

    @Override
    public void reset() {
        subModels.forEach(SubModel::reset);
        subModels.clear();
    }

    @Override
    public void use() {
        subModels.forEach(subModel -> subModel.draw(renderer));
    }

    @Override
    public int getHandle() {
        return handle;
    }

    public List<SubModel> getSubModels() {
        return subModels;
    }

    public AxisAlignedBoundingBox getAxisAlignedBoundingBox() {
        return axisAlignedBoundingBox;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Model model = (Model) o;

        if (handle != model.handle) return false;
        return subModels != null ? subModels.equals(model.subModels) : model.subModels == null;

    }

    @Override
    public int hashCode() {
        int result = handle;
        result = 31 * result + (subModels != null ? subModels.hashCode() : 0);
        return result;
    }
}