package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.graphics.GPUResource;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.homonoia.eris.resources.types.Stream;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Created by alexparlett on 16/04/2016.
 */
public class ShaderProgram extends Resource implements GPUResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShaderProgram.class);
    private int handle;
    private Map<String, Uniform> uniforms = new HashMap<>();
    private Renderer renderer;

    public ShaderProgram(final Context context) {
        super(context);
        renderer = context.getBean(Renderer.class);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {

        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("No root found"));

        String frag = Optional.ofNullable(root.getAsJsonPrimitive("frag"))
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new IOException("No 'frag' element found"));

        String vert = Optional.ofNullable(root.getAsJsonPrimitive("vert"))
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new IOException("No 'vert' element found"));

        ShaderPreprocessor shaderPreprocessor = new ShaderPreprocessor();

        StringBuffer fragBuffer, vertBuffer;
        try (InputStream fragStream = resourceCache.get(Stream.class, Paths.get(frag))
                .map(Stream::asInputStream)
                .orElseThrow(() -> new IOException(frag + " not found."))) {

            fragBuffer = shaderPreprocessor.process(fragStream, frag);
        }

        shaderPreprocessor.reset();

        try (InputStream vertStream = resourceCache.get(Stream.class, Paths.get(vert))
                .map(Stream::asInputStream)
                .orElseThrow(() -> new IOException(vert + " not found."))) {

            vertBuffer = shaderPreprocessor.process(vertStream, vert);
        }

        compile(fragBuffer, vertBuffer);
    }

    @Override
    public void use() {
        GL20.glUseProgram(handle);

        uniforms.values()
                .stream()
                .filter(uniform -> Objects.nonNull(uniform.getData()))
                .forEach(uniform -> renderer.bindUniform(uniform.getLocation(), uniform.getType(), uniform.getData()));
    }

    public Optional<Uniform> getUniform(String uniform) {
        return Optional.ofNullable(uniforms.get(uniform));
    }


    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public void reset() {
        if (handle != MemoryUtil.NULL) {
            GL20.glDeleteProgram(handle);
            handle = 0;
        }
    }

    private void compile(@Nonnull final StringBuffer fragBuffer, @Nonnull final StringBuffer vertBuffer) throws IOException {
        Objects.requireNonNull(fragBuffer);
        Objects.requireNonNull(vertBuffer);

        try (MemoryStack stack = stackPush()) {
            long win = GLFW.glfwGetCurrentContext();
            Graphics graphics = getContext().getBean(Graphics.class);
            glfwMakeContextCurrent(win != MemoryUtil.NULL ? win : graphics.getBackgroundWindow());

            int vertex = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
            GL20.glShaderSource(vertex, vertBuffer.toString());
            GL20.glCompileShader(vertex);


            IntBuffer success = stack.callocInt(1);
            GL20.glGetShaderiv(vertex, GL20.GL_COMPILE_STATUS, success);
            if (success.get() < 1) {
                String shaderInfoLog = GL20.glGetShaderInfoLog(vertex);

                GL20.glDeleteShader(vertex);
                glfwMakeContextCurrent(win);

                throw new IOException("Vertex Shader compile error\n" + shaderInfoLog);
            }

            int fragment = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            GL20.glShaderSource(fragment, fragBuffer.toString());
            GL20.glCompileShader(fragment);

            success.clear();
            GL20.glGetShaderiv(fragment, GL20.GL_COMPILE_STATUS, success);
            if (success.get() < 1) {
                String shaderInfoLog = GL20.glGetShaderInfoLog(fragment);

                GL20.glDeleteShader(fragment);
                glfwMakeContextCurrent(win);

                throw new IOException("Fragment Shader compile error\n" + shaderInfoLog);
            }

            handle = GL20.glCreateProgram();

            GL20.glAttachShader(handle, vertex);
            GL20.glAttachShader(handle, fragment);
            GL20.glLinkProgram(handle);

            success.clear();
            GL20.glGetProgramiv(handle, GL20.GL_LINK_STATUS, success);
            if (success.get() < 1) {
                String programInfoLog = GL20.glGetProgramInfoLog(handle);

                GL20.glDeleteShader(vertex);
                GL20.glDeleteShader(fragment);
                GL20.glDeleteProgram(handle);
                handle = 0;

                glfwMakeContextCurrent(win);

                throw new IOException("Fragment Shader link error\n" + programInfoLog);
            }

            GL20.glDeleteShader(vertex);
            GL20.glDeleteShader(fragment);

            IntBuffer type = stack.callocInt(1);
            IntBuffer size = stack.callocInt(1);
            for (int i = 0; i < GL20.glGetProgrami(handle, GL20.GL_ACTIVE_UNIFORMS); i++) {
                type.clear();
                size.clear();

                String name = glGetActiveUniform(handle, i, size, type);

                if (type.get(0) == GL20.GL_SAMPLER_2D || type.get(0) == GL20.GL_SAMPLER_CUBE) {
                    continue;
                }

                int location = glGetUniformLocation(handle, name);

                Uniform uniform = Uniform.builder()
                        .type(type.get())
                        .location(location)
                        .build();

                uniforms.put(name, uniform);
            }

            glfwMakeContextCurrent(win);
        }
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

                    try (InputStream includeStream = Files.newInputStream(Paths.get(filename))) {
                        out.append(process(includeStream, filename));
                    }
                } else {
                    out.append(line);
                    out.append(System.lineSeparator());
                }
            }

            return out;
        }

        public void reset() {
            includedFiles.clear();
        }
    }
}
