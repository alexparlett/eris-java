package org.homonoia.eris.resources.types;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.mesh.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.lwjgl.assimp.Assimp.AI_SCENE_FLAGS_INCOMPLETE;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenUVCoords;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_OptimizeGraph;
import static org.lwjgl.assimp.Assimp.aiProcess_OptimizeMeshes;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

/**
 * Created by alexparlett on 07/05/2016.
 */
@Slf4j
@Getter
public class Mesh extends Resource {

    private List<Vertex> vertices = new ArrayList<>();
    private List<Integer> indicies = new ArrayList<>();

    public Mesh(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream, "Input Stream must not be null.");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        byte[] buf = new byte[1024];
        while ((read = inputStream.read(buf)) >= 0) {
            baos.write(buf, 0, read);
        }

        if (baos.size() <= 0) {
            throw new IOException(MessageFormat.format("Failed to load Image {0}. File empty.", getLocation()));
        }

        ByteBuffer byteBuffer = memAlloc(baos.size());
        try {
            byteBuffer.put(baos.toByteArray());
            byteBuffer.flip();

            int pFlags = aiProcess_Triangulate |
                    aiProcess_GenNormals |
                    aiProcess_FixInfacingNormals |
                    aiProcess_GenUVCoords |
                    aiProcess_JoinIdenticalVertices |
                    aiProcess_OptimizeGraph |
                    aiProcess_OptimizeMeshes;

            AIScene aiScene = Assimp.aiImportFileFromMemory(byteBuffer, pFlags, "");

            if (isNull(aiScene) || aiScene.mFlags() == AI_SCENE_FLAGS_INCOMPLETE || isNull(aiScene.mRootNode())) {
                throw new IOException("Failed loading Model, " + Assimp.aiGetErrorString());
            }

            if (aiScene.mNumMeshes() <= 0) {
                throw new IOException("Failed loading Model, no meshes found.");
            }

            PointerBuffer meshes = aiScene.mMeshes();
            for (int i = 0; i < meshes.remaining(); i++) {
                AIMesh aiMesh = AIMesh.create(meshes.get(i));

                AIVector3D.Buffer aiVertices = aiMesh.mVertices();
                AIVector3D.Buffer aiNormals = aiMesh.mNormals();
                AIVector3D.Buffer aiTextureCoords = aiMesh.mTextureCoords().capacity() > 0 ? aiMesh.mTextureCoords(0) : null;
                for (int j = 0; j < aiVertices.remaining(); j++) {
                    AIVector3D vertex = aiVertices.get(j);
                    AIVector3D normal = aiNormals.get(j);

                    Vertex.Builder builder = Vertex.builder()
                            .position(new Vector3f(vertex.x(), vertex.y(), vertex.z()))
                            .normal(new Vector3f(normal.x(), normal.y(), normal.z()));

                    if (nonNull(aiTextureCoords)) {
                        AIVector3D texCoords = aiTextureCoords.get(j);
                        builder.texCoords(new Vector2f(texCoords.x(), texCoords.y()));
                    }
                    else {
                        builder.texCoords(new Vector2f(0.f));
                    }

                    vertices.add(builder.build());
                }

                AIFace.Buffer aiFaces = aiMesh.mFaces();
                for (int j = 0; j < aiFaces.remaining(); j++)
                {
                    AIFace aiFace = aiFaces.get(j);
                    IntBuffer aiIndices = aiFace.mIndices();
                    while(aiIndices.hasRemaining()) {
                        indicies.add(aiIndices.get());
                    }
                }
            }

            Assimp.aiReleaseImport(aiScene);
        } finally {
            memFree(byteBuffer);
        }

        setState(AsyncState.SUCCESS);
    }

    @Override
    public void reset() {
        vertices.clear();
        indicies.clear();
    }
}
