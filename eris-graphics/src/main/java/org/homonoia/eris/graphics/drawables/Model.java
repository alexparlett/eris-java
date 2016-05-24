package org.homonoia.eris.graphics.drawables;

import com.google.gson.*;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.exceptions.ParseException;
import org.homonoia.eris.graphics.GPUResource;
import org.homonoia.eris.graphics.drawables.model.SubModel;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.math.*;
import org.homonoia.eris.renderer.RenderKey;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.renderer.commands.Draw3dCommand;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.homonoia.eris.resources.types.Mesh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class Model extends Resource implements GPUResource {

    private int handle = UUID.randomUUID().hashCode();
    private List<SubModel> subModels = new ArrayList<>();

    public Model(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        Json json = new Json(getContext());
        json.load(inputStream);

        JsonArray root = json.getRoot().map(JsonElement::getAsJsonArray)
                .orElseThrow(() -> new IOException("no root found"));

        ResourceCache resourceCache = getContext().getComponent(ResourceCache.class);

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
                                return Vector3f.parse(string);
                            } catch (java.text.ParseException e) {
                                throw new ParseException(e);
                            }
                        })
                        .orElse(Vector3f.ZERO);

                Mesh mesh = Optional.ofNullable(subModelJson.getAsJsonPrimitive("mesh"))
                        .map(JsonPrimitive::getAsString)
                        .map(Paths::get)
                        .map(file -> resourceCache.get(Mesh.class, file))
                        .orElseThrow(() -> new ParseException("mesh specified for {0} not found", getPath()))
                        .orElseThrow(() -> new ParseException("mesh is required for models, e.g. 'mesh': 'Meshes/sphere.obj'"));

                Map<String, Uniform> uniforms = new HashMap<>();
                try {
                    Optional<JsonArray> maybeUniforms = Optional.ofNullable(subModelJson.getAsJsonArray("uniforms"));
                    maybeUniforms.ifPresent(ues -> ues.forEach(ue -> {
                        JsonObject object = ue.getAsJsonObject();

                        String type = Optional.ofNullable(object.getAsJsonPrimitive("type"))
                                .map(JsonPrimitive::getAsString)
                                .orElseThrow(() -> new JsonIOException("type element missing"));

                        String name = Optional.ofNullable(object.getAsJsonPrimitive("name"))
                                .map(JsonPrimitive::getAsString)
                                .orElseThrow(() -> new JsonIOException("name element missing"));

                        Object data = Optional.ofNullable(object.getAsJsonPrimitive("value"))
                                .map(jsonPrimitive -> {
                                    try {
                                        if (type == "float") {
                                            return jsonPrimitive.getAsFloat();
                                        } else if (type.equals("vec2")) {
                                            return Vector2f.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("vec3")) {
                                            return Vector3f.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("vec4")) {
                                            return Vector4f.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("int")) {
                                            return jsonPrimitive.getAsInt();
                                        } else if (type.equals("ivec2")) {
                                            return Vector2i.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("ivec3")) {
                                            return Vector3i.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("ivec4")) {
                                            return Vector4i.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("bool")) {
                                            return jsonPrimitive.getAsBoolean();
                                        } else if (type.equals("bvec2")) {
                                            return Vector2b.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("bvec3")) {
                                            return Vector3b.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("bvec4")) {
                                            return Vector4b.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("mat3")) {
                                            return Matrix3f.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("mat4")) {
                                            return Matrix4f.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("double")) {
                                            return jsonPrimitive.getAsDouble();
                                        } else if (type.equals("dvec2")) {
                                            return Vector2d.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("dvec3")) {
                                            return Vector3d.parse(jsonPrimitive.getAsString());
                                        } else if (type.equals("dvec4")) {
                                            return Vector4d.parse(jsonPrimitive.getAsString());
                                        } else {
                                            throw new JsonIOException("invalid value arg, type is not supported");
                                        }
                                    } catch (java.text.ParseException e) {
                                        throw new JsonIOException("invalid value arg, data invalid", e);
                                    }
                                })
                                .orElse(null);

                        buildUniform(name, data, uniforms, material.getShaderProgram());

                    }));
                } catch (JsonParseException ex) {
                    reset();
                    throw new ParseException("Parsing Uniform failed.", ex);
                }

                subModels.add(SubModel.builder()
                        .material(material)
                        .mesh(mesh)
                        .scale(scale)
                        .origin(origin)
                        .uniforms(uniforms)
                        .context(getContext())
                        .build());
            });
        } catch (ParseException | IllegalStateException ex) {
            reset();
            throw new IOException(ex);
        }
    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        subModels.forEach(SubModel::reset);
        subModels.clear();
    }

    @Override
    public void use() {
        Renderer renderer = getContext().getComponent(Renderer.class);
        subModels.forEach(subModel -> renderer.getState()
                .add(Draw3dCommand.builder()
                    .model(subModel)
                        .renderKey(RenderKey.builder()
                            .command(2)
                            .depth(1)
                            .transparency(0)
                            .extra(0)
                            .material(subModel.getMaterial().getHandle())
                            .target(getHandle())
                            .targetLayer(1)
                            .build())
                    .build()));
    }

    @Override
    public int getHandle() {
        return handle;
    }

    public List<SubModel> getSubModels() {
        return subModels;
    }

    private void buildUniform(final String uniform, final Object data, final Map<String, Uniform> uniforms, ShaderProgram shaderProgram)
    {
        Uniform uniformOptional = uniforms.get(uniform);
        if (Objects.nonNull(uniformOptional)) {
            uniformOptional.setData(data);
        }
        else
        {
            Optional<Uniform> shaderProgramUniform = shaderProgram.getUniform(uniform);
            if (shaderProgramUniform.isPresent()) {
                Uniform shaderUniform = shaderProgramUniform.get();
                Uniform modelUniform = Uniform.builder()
                        .location(shaderUniform.getLocation())
                        .type(shaderUniform.getType())
                        .data(data)
                        .build();

                uniforms.put(uniform, modelUniform);
            } else {
                throw new IllegalArgumentException("No uniforms in shader found for " + uniform);
            }
        }
    }
}
