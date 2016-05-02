package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.graphics.GPUResource;
import org.homonoia.eris.math.Vector2b;
import org.homonoia.eris.math.Vector3b;
import org.homonoia.eris.math.Vector4b;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.joml.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by alexparlett on 20/04/2016.
 */
public class Material extends Resource implements GPUResource {

    private int handle = UUID.randomUUID().hashCode();
    private ShaderProgram shaderProgram;
    private List<Uniform> uniforms = new ArrayList<>();
    private List<TextureUnit> textureUnits = new ArrayList<>();

    public Material(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot().map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("no root found"));

        ResourceCache resourceCache = getContext().getComponent(ResourceCache.class);

        shaderProgram = Optional.ofNullable(root.getAsJsonObject("program"))
                .map(JsonObject::getAsString)
                .map(prg -> Paths.get(prg))
                .map(prg -> resourceCache.get(ShaderProgram.class, prg))
                .orElseThrow(() -> new IOException("no program found"))
                .orElseThrow(() -> new IOException("Program not found in Resource Cache for Material " + getPath()));

        try {
            Optional.ofNullable(root.getAsJsonArray("textures"))
                    .orElseThrow(() -> new IOException("no textures element found"))
                    .forEach(jsonElement -> {
                        JsonObject object = jsonElement.getAsJsonObject();

                        String type = Optional.ofNullable(object.getAsJsonObject("type"))
                                .map(JsonObject::getAsString)
                                .orElseThrow(() -> new JsonIOException("type element missing"));

                        String uniform = Optional.ofNullable(object.getAsJsonObject("uniform"))
                                .map(JsonObject::getAsString)
                                .orElseThrow(() -> new JsonIOException("type element missing"));

                        Texture texture = Optional.ofNullable(object.getAsJsonObject("file"))
                                .map(JsonObject::getAsString)
                                .map(Paths::get)
                                .map(file -> {
                                    if ("2d".equals(type)) {
                                        return resourceCache.get(Texture2D.class, file)
                                                .orElseThrow(() -> new JsonIOException("file element invalid. not found."));
                                    } else if ("cube".equals(type)) {
                                        return resourceCache.get(TextureCube.class, file)
                                                .orElseThrow(() -> new JsonIOException("file element invalid. not found."));
                                    } else {
                                        throw new JsonIOException("invalid texture type arg, accepted vals (2d|cube)");
                                    }
                                })
                                .orElseThrow(() -> new JsonIOException("type element missing"));


                        textureUnits.add(TextureUnit.builder()
                                .texture(texture)
                                .uniform(uniform)
                                .build());
                    });
        } catch (JsonIOException ex) {
            throw new IOException("Parsing Texture Unit failed.", ex);
        }

        Optional<JsonArray> maybeUniforms = Optional.ofNullable(root.getAsJsonArray("uniforms"));
        maybeUniforms.ifPresent(ues -> ues.forEach(ue -> {
            JsonObject object = ue.getAsJsonObject();

            String type = Optional.ofNullable(object.getAsJsonObject("type"))
                    .map(JsonObject::getAsString)
                    .orElseThrow(() -> new JsonIOException("type element missing"));

            String name = Optional.ofNullable(object.getAsJsonObject("name"))
                    .map(JsonObject::getAsString)
                    .orElseThrow(() -> new JsonIOException("name element missing"));

            Object data = Optional.ofNullable(object.getAsJsonObject("value"))
                    .map(jsonObject -> {
                        if (type == "float") {
                            return jsonObject.getAsFloat();
                        } else if (type == "vec2") {
                            return Vector2f.parse(jsonObject.getAsString());
                        } else if (type == "vec3") {
                            return Vector3f.parse(jsonObject.getAsString());
                        } else if (type == "vec4") {
                            return Vector4f.parse(jsonObject.getAsString());
                        } else if (type == "int") {
                            return jsonObject.getAsInt();
                        } else if (type == "ivec2") {
                            return Vector2i.parse(jsonObject.getAsString());
                        } else if (type == "ivec3") {
                            return Vector3i.parse(jsonObject.getAsString());
                        } else if (type == "ivec4") {
                            return Vector4i.parse(jsonObject.getAsString());
                        } else if (type == "bool") {
                            return jsonObject.getAsBoolean();
                        } else if (type == "bvec2") {
                            return Vector2b.parse(jsonObject.getAsString());
                        } else if (type == "bvec3") {
                            return Vector3b.parse(jsonObject.getAsString());
                        } else if (type == "bvec4") {
                            return Vector4b.parse(jsonObject.getAsString());
                        } else if (type == "mat3") {
                            return Matrix3f.parse(jsonObject.getAsString());
                        } else if (type == "mat4") {
                            return Matrix4f.parse(jsonObject.getAsString());
                        } else {
                            throw new JsonIOException("invalid value arg, type is not supported");
                        }
                    })
                    .orElse(null);

            setUniform(name, type, data);
        }));
    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void use() {

    }

    @Override
    public int getHandle() {
        return handle;
    }
}
