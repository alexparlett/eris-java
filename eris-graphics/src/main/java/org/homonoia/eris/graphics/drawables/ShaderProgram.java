package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.graphics.GPUResource;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.renderer.Renderer;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.Json;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

/**
 * Created by alexparlett on 16/04/2016.
 */
public class ShaderProgram extends Resource implements GPUResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShaderProgram.class);
    private int handle;
    private Map<String, Uniform> uniforms = new HashMap<>();

    public final static class ShaderPreprocessor {

        private Set<String> includedFiles = new HashSet<>();

        public StringBuffer process(final InputStream shaderStream, final String shaderFilename) throws IOException {

            includedFiles.add(shaderFilename);

            BufferedReader in = new BufferedReader(new InputStreamReader(shaderStream));
            StringBuffer out = new StringBuffer();

            String line;
            while((line = in.readLine()) != null) {
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

    public ShaderProgram(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("No root found"));

        String frag = Optional.ofNullable(root.getAsJsonObject("frag"))
                .map(JsonObject::getAsString)
                .orElseThrow(() -> new IOException("No 'frag' element found"));

        String vert = Optional.ofNullable(root.getAsJsonObject("vert"))
                .map(JsonObject::getAsString)
                .orElseThrow(() -> new IOException("No 'vert' element found"));

        ShaderPreprocessor shaderPreprocessor = new ShaderPreprocessor();

        StringBuffer fragBuffer, vertBuffer;
        try (InputStream fragStream = Files.newInputStream(Paths.get(frag))) {
            fragBuffer = shaderPreprocessor.process(fragStream, frag);
        }

        shaderPreprocessor.reset();

        try (InputStream vertStream = Files.newInputStream(Paths.get(vert))) {
            vertBuffer = shaderPreprocessor.process(vertStream, vert);
        }

        compile(fragBuffer, vertBuffer);
    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void use() {
        Renderer renderer = getContext().getComponent(Renderer.class);

        GL20.glUseProgram(handle);

        uniforms.forEach((name, uniform) -> {
            if (uniform.getData() != null) {
                renderer.bindUniform(uniform.getLocation(), uniform.getType(), uniform.getData());
            }
        });
    }

    public void setUniform(String uniform, final Object data)
    {
        Optional.ofNullable(uniforms.get(uniform))
                .ifPresent(shaderUniform -> shaderUniform.setData(data));
    }

    public Optional<Uniform> getUniform(String uniform)
    {
        return Optional.ofNullable(uniforms.get(uniform));
    }

    public void removeUniform(String uniform)
    {
        uniforms.remove(uniform);
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (handle != MemoryUtil.NULL) {
            GL20.glDeleteProgram(handle);
            handle = 0;
        }
    }

    private void compile(final StringBuffer fragBuffer, final StringBuffer vertBuffer) throws IOException {
        Objects.requireNonNull(fragBuffer);
        Objects.requireNonNull(vertBuffer);

        long win = GLFW.glfwGetCurrentContext();
        Graphics graphics = getContext().getComponent(Graphics.class);
        glfwMakeContextCurrent(win != MemoryUtil.NULL ? win : graphics.getBackgroundWindow());

        int vertex = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertex, vertBuffer.toString());
        GL20.glCompileShader(vertex);

        IntBuffer success = BufferUtils.createIntBuffer(1);
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

        success.reset();
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

        success.reset();
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

        IntBuffer uniformCount = BufferUtils.createIntBuffer(1);
        IntBuffer type = BufferUtils.createIntBuffer(1);
        IntBuffer size = BufferUtils.createIntBuffer(1);
        IntBuffer length = BufferUtils.createIntBuffer(1);
        ByteBuffer name = BufferUtils.createByteBuffer(64);
        GL20.glGetProgramiv(handle, GL20.GL_ACTIVE_UNIFORMS, uniformCount);
        for (int i = 0; i < uniformCount.get(); i++) {
            type.reset();
            size.reset();
            name.reset();
            length.reset();

            GL20.glGetActiveUniform(handle, i, length, size, type, name);

            if (type.get() == GL20.GL_SAMPLER_2D || type.get() == GL20.GL_SAMPLER_CUBE) {
                continue;
            }

            Uniform uniform = Uniform.builder()
                    .type(type.get())
                    .location(i)
                    .build();

            uniforms.put(name.asCharBuffer().toString(), uniform);
        }

        glfwMakeContextCurrent(win);
    }
}
