package org.homonoia.eris.graphics.drawables.model;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.graphics.Graphics;
import org.homonoia.eris.graphics.drawables.Material;
import org.homonoia.eris.graphics.drawables.sp.Uniform;
import org.homonoia.eris.math.Vector3f;
import org.homonoia.eris.resources.types.Mesh;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class SubModel extends Contextual {

    private GenerationState generationState = GenerationState.LOADER;
    private Material material;
    private IntBuffer indices;
    private FloatBuffer vertices;
    private Mesh mesh;
    private float scale;
    private Vector3f origin;
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
        this.indices = builder.indices;
        this.vertices = builder.vertices;
        this.uniforms = builder.uniforms;
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

    public void setUniform(final String uniform, final Object data)
    {
        Optional<Uniform> uniformOptional = getUniform(uniform);
        if (uniformOptional.isPresent()) {
            uniformOptional.get().setData(data);
        }
        else
        {
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

    public Optional<Uniform> getUniform(final String uniform)
    {
        return Optional.ofNullable(uniforms.get(uniform));
    }

    public void removeUniform(final String uniform)
    {
        uniforms.remove(uniform);
    }

    public void compile() {
        long win = GLFW.glfwGetCurrentContext();
        Graphics graphics = getContext().getComponent(Graphics.class);
        glfwMakeContextCurrent(win != MemoryUtil.NULL ? win : graphics.getBackgroundWindow());

        if (win != MemoryUtil.NULL && win == graphics.getRenderWindow())
            generationState = GenerationState.RENDERER;
        else
            generationState = GenerationState.LOADER;

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
        GL11.glDrawElements(GL11.GL_TRIANGLES, indices.capacity(), GL11.GL_UNSIGNED_BYTE, 0);
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
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glVertexAttribPointer(0, 4, GL_FLOAT, false, Vertex.SIZE_OF, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, Vertex.SIZE_OF, Vertex.BYTE_SIZE * 4);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, Vertex.SIZE_OF, Vertex.BYTE_SIZE * 7);

        glBindVertexArray(0);
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
        private FloatBuffer vertices;
        private IntBuffer indices;
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

            List<Integer> indices = calculateIndices();
            this.indices = BufferUtils.createIntBuffer(indices.size());
            indices.forEach(this.indices::put);
            this.indices.flip();

            List<Vertex> vertices = calculateVertices();
            this.vertices = BufferUtils.createFloatBuffer(vertices.size() * Vertex.COUNT);
            vertices.forEach(vertex -> {
                this.vertices.put(vertex.getPosition());
                this.vertices.put(vertex.getNormal());
                this.vertices.put(vertex.getTexCoords());
            });
            this.vertices.flip();

            return new SubModel(this);
        }

        private List<Vertex> calculateVertices() {
            List<Vertex> vertices = new ArrayList<>();
            for(int i = 0; i < mesh.getGeometry().size(); i++) {

                Vector3f position = (Vector3f) mesh.getGeometry().get(i)
                        .mul(scale)
                        .add(origin);

                Vector3f normal = mesh.getNormals().get(i);

                Vector3f texCoord;
                if (!mesh.getTextureCoords().isEmpty()) {
                    texCoord = mesh.getTextureCoords().get(i);
                } else {
                    texCoord = Vector3f.ZERO;
                }

                vertices.add(Vertex.builder()
                        .position(new float[] { position.x, position.y, position.z, 1.f })
                        .normal(new float[] { normal.x, normal.y, normal.z })
                        .texCoords(new float[] { texCoord.x, texCoord.y })
                        .build());
            }
            return vertices;
        }

        private List<Integer> calculateIndices() {
            List<Integer> indices = new ArrayList<>();
            mesh.getFaces().forEach(face -> face.getIndicies().forEach(indices::add));
            return indices;
        }
    }
}
