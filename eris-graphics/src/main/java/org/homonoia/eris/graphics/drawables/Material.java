package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.exceptions.ErisRuntimeExcecption;
import org.homonoia.eris.core.parsers.Matrix3fParser;
import org.homonoia.eris.core.parsers.Matrix4fParser;
import org.homonoia.eris.core.parsers.Vector2dParser;
import org.homonoia.eris.core.parsers.Vector2fParser;
import org.homonoia.eris.core.parsers.Vector2iParser;
import org.homonoia.eris.core.parsers.Vector3dParser;
import org.homonoia.eris.core.parsers.Vector3fParser;
import org.homonoia.eris.core.parsers.Vector3iParser;
import org.homonoia.eris.core.parsers.Vector4dParser;
import org.homonoia.eris.core.parsers.Vector4fParser;
import org.homonoia.eris.core.parsers.Vector4iParser;
import org.homonoia.eris.graphics.drawables.material.CullMode;
import org.homonoia.eris.graphics.drawables.material.TextureUnit;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.resources.GPUResource;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.lwjgl.opengl.GL13;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.glCullFace;

/**
 * Created by alexparlett on 20/04/2016.
 */
public class Material extends Resource implements GPUResource {

    private static int uuid = 1;

    private int handle = uuid++;
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

        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);

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
                                    return Vector2fParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("vec3")) {
                                    return Vector3fParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("vec4")) {
                                    return Vector4fParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("int")) {
                                    return jsonPrimitive.getAsInt();
                                } else if (type.equals("ivec2")) {
                                    return Vector2iParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("ivec3")) {
                                    return Vector3iParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("ivec4")) {
                                    return Vector4iParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("bool")) {
                                    return jsonPrimitive.getAsBoolean();
                                } else if (type.equals("mat3")) {
                                    return Matrix3fParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("mat4")) {
                                    return Matrix4fParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("double")) {
                                    return jsonPrimitive.getAsDouble();
                                } else if (type.equals("dvec2")) {
                                    return Vector2dParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("dvec3")) {
                                    return Vector3dParser.parse(jsonPrimitive.getAsString());
                                } else if (type.equals("dvec4")) {
                                    return Vector4dParser.parse(jsonPrimitive.getAsString());
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

        setState(AsyncState.GPU_READY);
    }

    @Override
    public void compile() throws IOException {
        if (shaderProgram.getState().equals(AsyncState.GPU_READY)) {
            shaderProgram.compile();
        }

        for(TextureUnit textureUnit : textureUnits) {
            if (textureUnit.getTexture().getState().equals(AsyncState.GPU_READY)) {
                textureUnit.getTexture().compile();
            }
        }

        setState(AsyncState.SUCCESS);
    }

    @Override
    public void use() {
        glCullFace(cullMode.getGlCull());

        shaderProgram.use();

        textureUnits.stream()
                .filter(textureUnit -> textureUnit.getTexture() != null)
                .forEach(textureUnit -> {
                    GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit.getUnit());
                    textureUnit.getTexture().use();
                    Uniform uniform = shaderProgram.getUniform(textureUnit.getUniform()).orElseThrow(() -> new ErisRuntimeExcecption("Could not bind {} on material {}", textureUnit.getUniform(), getPath()));
                    uniform.bindUniform(textureUnit.getUnit());
                });

        uniforms.values().stream()
                .filter(uniform -> Objects.nonNull(uniform.getData()))
                .forEach(Uniform::bindUniform);
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
            Uniform shaderUniform = shaderProgramUniform.orElseThrow(() -> new IllegalArgumentException("No uniforms in shader found for " + uniform));
            Uniform modelUniform = Uniform.builder()
                    .location(shaderUniform.getLocation())
                    .type(shaderUniform.getType())
                    .data(data)
                    .build();

            uniforms.put(uniform, modelUniform);
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
