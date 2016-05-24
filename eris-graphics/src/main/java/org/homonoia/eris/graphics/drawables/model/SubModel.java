package org.homonoia.eris.graphics.drawables.model;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.Material;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.math.Vector3f;
import org.homonoia.eris.resources.types.Mesh;
import org.homonoia.eris.resources.types.mesh.Face;
import org.homonoia.eris.resources.types.mesh.Vertex;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class SubModel extends Contextual {

    private GenerationState generationState = GenerationState.LOADER;
    private Material material;
    private IntBuffer indices;
    private FloatBuffer vertices;
    private Mesh mesh;
    private float scale = 1.f;
    private Vector3f origin = Vector3f.ZERO;
    private Map<String, Uniform> uniforms;
    private int vao = 0;
    private int vbo = 0;
    private int ebo = 0;

    private SubModel(Builder builder) {
        super(builder.context);
        this.material = builder.material;
        this.mesh = builder.mesh;
        this.scale = builder.scale;
        this.origin = builder.origin;
        this.uniforms = builder.uniforms;

        compile();
    }

    public Material getMaterial() {
        return material;
    }

    public IntBuffer getIndices() {
        return indices;
    }

    public FloatBuffer getVertices() {
        return vertices;
    }

    public GenerationState getGenerationState() {
        return generationState;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public int getVao() {
        return vao;
    }

    public int getVbo() {
        return vbo;
    }

    public int getEbo() {
        return ebo;
    }

    public Map<String, Uniform> getUniforms() {
        return uniforms;
    }

    public void setUniforms(final Map<String, Uniform> uniforms) {
        this.uniforms = uniforms;
    }

    public void setUniform(final String uniform, final Object data) {
        Optional<Uniform> uniformOptional = getUniform(uniform);
        if (uniformOptional.isPresent()) {
            uniformOptional.get().setData(data);
        } else {
            Optional<Uniform> shaderProgramUniform = getMaterial().getShaderProgram().getUniform(uniform);
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

    public Optional<Uniform> getUniform(final String uniform) {
        return Optional.ofNullable(uniforms.get(uniform));
    }

    public void removeUniform(final String uniform) {
        uniforms.remove(uniform);
    }

    public void compile() {
        long win = GLFW.glfwGetCurrentContext();
        Graphics graphics = getContext().getComponent(Graphics.class);
        glfwMakeContextCurrent(win != MemoryUtil.NULL ? win : graphics.getBackgroundWindow());

        if (win != MemoryUtil.NULL && win == graphics.getRenderWindow()) {
            generationState = GenerationState.RENDERER;
        } else {
            generationState = GenerationState.LOADER;
        }

        int currentUniqueVertexIndex = 0;
        Map<Vertex, Integer> indexMap = new HashMap<>();
        List<Vertex> vertexList = new ArrayList<>();
        for (Face face : mesh.getFaces()) {
            for (Vertex vertex : face.getVertices()) {
                if (!indexMap.containsKey(vertex)) {
                    indexMap.put(vertex, currentUniqueVertexIndex++);
                    vertexList.add(vertex);
                }
            }
        }

        vertices = BufferUtils.createFloatBuffer(vertexList.size() * Vertex.COUNT);
        vertexList.forEach(vertex -> {
            vertices.put((vertex.getPosition().x + origin.x) * scale);
            vertices.put((vertex.getPosition().y + origin.y) * scale);
            vertices.put((vertex.getPosition().z + origin.z) * scale);

            vertices.put(vertex.getNormal().x);
            vertices.put(vertex.getNormal().y);
            vertices.put(vertex.getNormal().z);

            vertices.put(vertex.getTexCoords().x);
            vertices.put(vertex.getTexCoords().y);
        });
        vertices.flip();

        indices = BufferUtils.createIntBuffer(mesh.getFaces().size() * 3);
        for (Face face : mesh.getFaces()) {
            for (Vertex vertex : face.getVertices()) {
                indices.put(indexMap.get(vertex));
            }
        }
        indices.flip();

        compileInternal();

        glfwMakeContextCurrent(win);
    }

    public void draw() {
        if (!generationState.equals(GenerationState.RENDERER)) {
            compileInternal();
            generationState = GenerationState.RENDERER;
        }

        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(vao);
        GL11.glDrawElements(GL11.GL_TRIANGLES, indices.capacity(), GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    public void reset() {
        if (Objects.nonNull(material)) material.release();
        if (Objects.nonNull(indices)) indices.clear();
        if (Objects.nonNull(vertices)) vertices.clear();
        if (Objects.nonNull(mesh)) mesh.release();
        if (vao != 0) glDeleteVertexArrays(vao);
        if (vbo != 0) glDeleteBuffers(vbo);
        if (ebo != 0) glDeleteBuffers(ebo);
    }

    private void compileInternal() {
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

        ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, Vertex.SIZE_OF, 0);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, Vertex.SIZE_OF, 3 * Float.BYTES);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, Vertex.SIZE_OF, 6 * Float.BYTES);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Material material;
        private Mesh mesh;
        private float scale = 1.f;
        private Vector3f origin = Vector3f.ZERO;
        private Context context;
        private Map<String, Uniform> uniforms;

        private Builder() {
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder mesh(Mesh mesh) {
            this.mesh = mesh;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder origin(Vector3f origin) {
            this.origin = origin;
            return this;
        }

        public Builder uniforms(Map<String, Uniform> uniforms) {
            this.uniforms = uniforms;
            return this;
        }

        public SubModel build() {
            Objects.requireNonNull(context);
            Objects.requireNonNull(mesh);
            Objects.requireNonNull(material);
            return new SubModel(this);
        }
    }
}
