package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.graphics.drawables.sp.Attribute;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.resources.GPUResource;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.homonoia.eris.resources.types.Stream;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.lang.System.lineSeparator;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.homonoia.eris.graphics.drawables.sp.Uniform.builder;
import static org.homonoia.eris.resources.Resource.AsyncState.GPU_READY;
import static org.homonoia.eris.resources.Resource.AsyncState.SUCCESS;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_ATTRIBUTES;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetActiveAttrib;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/04/2017
 */
public class ShaderProgram extends Resource implements GPUResource {

    private static final Logger LOG = getLogger(ShaderProgram.class);
    private int handle;
    private Map<String, Uniform> uniforms = new HashMap<>();
    private Map<String, Attribute> attributes = new HashMap<>();
    private StringBuffer vertBuffer;
    private StringBuffer fragBuffer;

    public ShaderProgram(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("No root found"));

        String frag = ofNullable(root.getAsJsonPrimitive("frag"))
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new IOException("No 'frag' element found"));

        String vert = ofNullable(root.getAsJsonPrimitive("vert"))
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new IOException("No 'vert' element found"));

        ShaderPreprocessor shaderPreprocessor = new ShaderPreprocessor();

        try (InputStream fragStream = resourceCache.get(Stream.class, get(frag))
                .map(Stream::asInputStream)
                .orElseThrow(() -> new IOException(frag + " not found."))) {

            fragBuffer = shaderPreprocessor.process(fragStream, frag);
        }

        shaderPreprocessor.reset();

        try (InputStream vertStream = resourceCache.get(Stream.class, get(vert))
                .map(Stream::asInputStream)
                .orElseThrow(() -> new IOException(vert + " not found."))) {

            vertBuffer = shaderPreprocessor.process(vertStream, vert);
        }

        setState(GPU_READY);
    }

    @Override
    public void use() {
        glUseProgram(handle);

        uniforms.values()
                .stream()
                .filter(uniform -> nonNull(uniform.getData()))
                .forEach(Uniform::bindUniform);
    }

    public Optional<Uniform> getUniform(String uniform) {
        return ofNullable(uniforms.get(uniform));
    }

    public Optional<Attribute> getAttribute(String name) {
        return ofNullable(attributes.get(name));
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public void reset() {
        if (handle != NULL) {
            glDeleteProgram(handle);
            handle = 0;
        }
    }

    @Override
    public void compile() throws IOException {
        requireNonNull(fragBuffer);
        requireNonNull(vertBuffer);

        try (MemoryStack stack = stackPush()) {
            int vertex = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertex, vertBuffer.toString());
            glCompileShader(vertex);


            IntBuffer success = stack.callocInt(1);
            glGetShaderiv(vertex, GL_COMPILE_STATUS, success);
            if (success.get() < 1) {
                String shaderInfoLog = glGetShaderInfoLog(vertex);

                glDeleteShader(vertex);
                throw new IOException("Vertex Shader compile error\n" + shaderInfoLog);
            }

            int fragment = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragment, fragBuffer.toString());
            glCompileShader(fragment);

            success.clear();
            glGetShaderiv(fragment, GL_COMPILE_STATUS, success);
            if (success.get() < 1) {
                String shaderInfoLog = glGetShaderInfoLog(fragment);

                glDeleteShader(fragment);
                throw new IOException("Fragment Shader compile error\n" + shaderInfoLog);
            }

            handle = glCreateProgram();

            glAttachShader(handle, vertex);
            glAttachShader(handle, fragment);
            glLinkProgram(handle);

            success.clear();
            glGetProgramiv(handle, GL_LINK_STATUS, success);
            if (success.get() < 1) {
                String programInfoLog = glGetProgramInfoLog(handle);

                glDeleteShader(vertex);
                glDeleteShader(fragment);
                glDeleteProgram(handle);
                handle = 0;

                throw new IOException("Shader link error\n" + programInfoLog);
            }

            glDeleteShader(vertex);
            glDeleteShader(fragment);

            IntBuffer type = stack.callocInt(1);
            IntBuffer size = stack.callocInt(1);
            for (int i = 0; i < glGetProgrami(handle, GL_ACTIVE_UNIFORMS); i++) {
                type.clear();
                size.clear();

                String name = glGetActiveUniform(handle, i, size, type);
                int location = glGetUniformLocation(handle, name);

                Uniform uniform = builder()
                        .type(type.get())
                        .location(location)
                        .build();

                uniforms.put(name, uniform);
            }

            for (int i = 0; i < glGetProgrami(handle, GL_ACTIVE_ATTRIBUTES); i++) {
                type.clear();
                size.clear();

                String name = glGetActiveAttrib(handle, i, size, type);
                int location = glGetAttribLocation(handle, name);

                Attribute attribute = Attribute.builder()
                        .type(type.get())
                        .location(location)
                        .build();

                attributes.put(name, attribute);
            }
        }

        setState(SUCCESS);
    }

    public final static class ShaderPreprocessor {

        private Set<String> includedFiles = new HashSet<>();

        public StringBuffer process(final InputStream shaderStream, final String shaderFilename) throws IOException {

            includedFiles.add(shaderFilename);

            BufferedReader in = new BufferedReader(new InputStreamReader(shaderStream));
            StringBuffer out = new StringBuffer();

            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("#include")) {
                    String[] tokens = line.split(" ");
                    String filename;
                    if (tokens.length < 2 || (filename = tokens[1].trim()).isEmpty()) {
                        throw new IOException("Failed to process " + shaderFilename + " include statement must specify a file");
                    }

                    if (includedFiles.contains(filename)) {
                        LOG.warn("Processing {}, {} included multiple times in hierarchy", shaderFilename, filename);
                        continue;
                    }

                    try (InputStream includeStream = newInputStream(get(filename))) {
                        out.append(process(includeStream, filename));
                    }
                } else {
                    out.append(line);
                    out.append(lineSeparator());
                }
            }

            return out;
        }

        public void reset() {
            includedFiles.clear();
        }
    }
}
