package org.homonoia.eris.graphics.drawables;

import com.google.gson.*;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.graphics.GPUResource;
import org.homonoia.eris.graphics.drawables.material.CullMode;
import org.homonoia.eris.graphics.drawables.material.TextureUnit;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.math.*;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;

/**
 * Created by alexparlett on 20/04/2016.
 */
public class Material extends Resource implements GPUResource {

    private int handle = UUID.randomUUID().hashCode();
    private ShaderProgram shaderProgram;
    private Map<String, Uniform> uniforms = new HashMap<>();
    private List<TextureUnit> textureUnits = new ArrayList<>();
    private CullMode cullMode;

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

        shaderProgram = Optional.ofNullable(root.getAsJsonPrimitive("program"))
                .map(JsonPrimitive::getAsString)
                .map(prg -> Paths.get(prg))
                .map(prg -> resourceCache.get(ShaderProgram.class, prg))
                .orElseThrow(() -> new IOException("no program found"))
                .orElseThrow(() -> new IOException("Program not found in Resource Cache for Material " + getPath()));

        try {
            Optional<JsonArray> textures = Optional.ofNullable(root.getAsJsonArray("textures"));
            textures.ifPresent(jsonElements -> {
                jsonElements.forEach(jsonElement -> {
                    JsonObject object = jsonElement.getAsJsonObject();

                    String type = Optional.ofNullable(object.getAsJsonPrimitive("type"))
                            .map(JsonPrimitive::getAsString)
                            .orElseThrow(() -> new JsonIOException("type element missing"));

                    String uniform = Optional.ofNullable(object.getAsJsonPrimitive("uniform"))
                            .map(JsonPrimitive::getAsString)
                            .orElseThrow(() -> new JsonIOException("type element missing"));

                    int unit = Optional.ofNullable(object.getAsJsonPrimitive("unit"))
                            .map(JsonPrimitive::getAsInt)
                            .orElseThrow(() -> new JsonIOException("unit element missing"));

                    if (unit < 0 || unit > 31) {
                        throw new JsonIOException("invalid texture unit arg, accepet vals between 0 and 31 inclusive");
                    }

                    Texture texture = Optional.ofNullable(object.getAsJsonPrimitive("file"))
                            .map(JsonPrimitive::getAsString)
                            .map(Paths::get)
                            .map(getTexture(resourceCache, type))
                            .orElseThrow(() -> new JsonIOException("type element missing"));


                    textureUnits.add(TextureUnit.builder()
                            .unit(unit)
                            .texture(texture)
                            .uniform(uniform)
                            .build());
                });
            });
        } catch (JsonParseException ex) {
            reset();
            throw new IOException("Parsing Texture Unit failed.", ex);
        }

        try {
            Optional<JsonArray> maybeUniforms = Optional.ofNullable(root.getAsJsonArray("uniforms"));
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
                            } catch (ParseException e) {
                                throw new JsonIOException("invalid value arg, data invalid", e);
                            }
                        })
                        .orElse(null);

                setUniform(name, data);

            }));
        } catch (JsonParseException ex) {
            reset();
            throw new IOException("Parsing Uniform failed.", ex);
        }

        cullMode = Optional.ofNullable(root.getAsJsonPrimitive("cullMode"))
                .map(JsonPrimitive::getAsString)
                .map(CullMode::parse)
                .orElse(CullMode.Back);
    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void use() {
        Renderer renderer = getContext().getComponent(Renderer.class);

        GL11.glCullFace(cullMode.getGlCull());

        shaderProgram.use();

        textureUnits.stream()
                .filter(textureUnit -> textureUnit.getTexture() != null)
                .forEach(textureUnit -> {
                    GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit.getUnit());
                    textureUnit.getTexture().use();
                    renderer.bindUniform(GL20.glGetUniformLocation(shaderProgram.getHandle(), textureUnit.getUniform()), GL11.GL_INT, textureUnit.getUnit());
                });

        uniforms.values().stream()
                .filter(uniform -> Objects.nonNull(uniform.getData()))
                .forEach(uniform -> renderer.bindUniform(uniform.getLocation(), uniform.getType(), uniform.getData()));
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public void reset() {
        if (Objects.nonNull(shaderProgram)) {
            shaderProgram.release();
            shaderProgram = null;
        }

        textureUnits.stream().map(TextureUnit::getTexture).forEach(Texture::release);
        textureUnits.clear();
    }

    public CullMode getCullMode() {
        return cullMode;
    }

    public void setCullMode(final CullMode cullMode) {
        this.cullMode = cullMode;
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(final ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public List<TextureUnit> getTextureUnits() {
        return textureUnits;
    }

    public void setTextureUnits(final List<TextureUnit> textureUnits) {
        this.textureUnits = textureUnits;
    }

    public Map<String, Uniform> getUniforms() {
        return uniforms;
    }

    public void setUniforms(final Map<String, Uniform> uniforms) {
        this.uniforms = uniforms;
    }

    public void setUniform(final String uniform, final Object data)
    {
        Optional<Uniform> uniformOptional = getUniform(uniform);
        if (uniformOptional.isPresent()) {
            uniformOptional.get().setData(data);
        }
        else
        {
            Optional<Uniform> shaderProgramUniform = shaderProgram.getUniform(uniform);
            if (shaderProgramUniform.isPresent()) {
                Uniform shaderUniform = shaderProgramUniform.get();
                Uniform materialUniform = Uniform.builder()
                        .location(shaderUniform.getLocation())
                        .type(shaderUniform.getType())
                        .data(data)
                        .build();

                uniforms.put(uniform, materialUniform);
            } else {
                throw new IllegalArgumentException("No uniforms in shader found for " + uniform);
            }
        }
    }

    public Optional<Uniform> getUniform(final String uniform)
    {
        return Optional.ofNullable(uniforms.get(uniform));
    }

    public void removeUniform(final String uniform)
    {
        uniforms.remove(uniform);
    }

    private Function<Path, Texture> getTexture(final ResourceCache resourceCache, final String type) throws JsonIOException {
        return file -> {
            if ("2d".equals(type)) {
                return resourceCache.get(Texture2D.class, file)
                        .orElseThrow(() -> new JsonIOException("file element invalid. not found."));
            } else if ("cube".equals(type)) {
                return resourceCache.get(TextureCube.class, file)
                        .orElseThrow(() -> new JsonIOException("file element invalid. not found."));
            } else {
                throw new JsonIOException("invalid texture type arg, accepted vals (2d|cube)");
            }
        };
    }
}
