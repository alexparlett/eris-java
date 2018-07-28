package org.homonoia.sw.assets.loaders;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.ogex.GeometryObjectStructure;
import org.homonoia.sw.assets.loaders.ogex.MeshStructure;
import org.homonoia.sw.assets.loaders.ogex.MeshType;
import org.homonoia.sw.assets.loaders.ogex.OgexParser;
import org.homonoia.sw.assets.loaders.ogex.OgexScene;
import org.homonoia.sw.assets.loaders.ogex.VertexArrayStructure;
import org.homonoia.sw.collections.Collectors;
import org.homonoia.sw.collections.Streams;

import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 27/07/2018
 */
public class OgexLoader extends ModelLoader<OgexLoader.OgexLoaderParameters> {

    public static class OgexLoaderParameters extends ModelLoader.ModelParameters {

    }

    public OgexLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    private OgexParser ogexParser = new OgexParser();

    @Override
    public ModelData loadModelData(FileHandle fileHandle, OgexLoaderParameters parameters) {

        try {
            ModelData modelData = new ModelData();

            OgexScene ogex = ogexParser.parseScene(fileHandle.readString(StandardCharsets.UTF_8.name()));

            modelData.nodes.ensureCapacity(ogex.getNodes().size());
            modelData.meshes.ensureCapacity(ogex.getGeometry().size());

            final AtomicInteger id = new AtomicInteger(0);

            ogex.getNodes().forEach(geometryNodeStructure -> {
                GeometryObjectStructure geometryObjectStructure = ogex.getRootStructure().getStructure(geometryNodeStructure.getObjectReference())
                        .map(GeometryObjectStructure.class::cast)
                        .orElseThrow(() -> new OpenDDLException("Reference not found"));

                MeshStructure mesh = geometryObjectStructure.getMesh();

                Array<VertexAttribute> vertexAttributes = Streams.of(mesh.getVertexArrays())
                        .map(vertexArrayStructure -> {
                            switch (vertexArrayStructure.getAttribute()) {
                                case "position":
                                    return new VertexAttribute(VertexAttributes.Usage.Position, vertexArrayStructure.getVertexSize(), ShaderProgram.POSITION_ATTRIBUTE);
                                case "normal":
                                    return new VertexAttribute(VertexAttributes.Usage.Normal, vertexArrayStructure.getVertexSize(), ShaderProgram.NORMAL_ATTRIBUTE);
                                case "color":
                                    return new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, vertexArrayStructure.getVertexSize(), ShaderProgram.COLOR_ATTRIBUTE);
                                case "texcoord":
                                    return new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, vertexArrayStructure.getVertexSize(), ShaderProgram.TEXCOORD_ATTRIBUTE + vertexArrayStructure.getIndex());
                                default:
                                    return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toArray());

                float[] finalVertices = new float[mesh.getVertexSize() * mesh.getNumberOfVertices()];
                for (int i = 0, vi = 0; i < mesh.getNumberOfVertices(); i++) {
                    for (int j = 0; j < mesh.getVertexArrays().size; j++) {
                        VertexArrayStructure vertexArrayStructure = mesh.getVertexArrays().get(j);
                        for (int k = 0; k < vertexArrayStructure.getVertexSize(); k++) {
                            finalVertices[vi++] = vertexArrayStructure.getVertices().get(i)[k];
                        }
                    }
                }

                String stringId = Integer.toString(id.incrementAndGet());

                ModelMeshPart modelMeshPart = new ModelMeshPart();
                modelMeshPart.id = nonNull(mesh.getStructureName()) ? mesh.getStructureName() : "$mesh" + stringId;
                modelMeshPart.primitiveType = parseType(mesh.getPrimitive());
                modelMeshPart.indices = mesh.getIndexArray()
                        .map(indexArray -> {
                            ShortBuffer finalIndicies = ShortBuffer.allocate(indexArray.getIndicies().size * indexArray.getIndexSize());
                            indexArray.getIndicies().forEach(longs -> {
                                for (int i = 0; i < indexArray.getIndexSize(); i++) {
                                    finalIndicies.put((short) longs[i]);
                                }
                            });
                            return finalIndicies.array();
                        })
                        .orElse(null);

                ModelMesh modelMesh = new ModelMesh();
                modelMesh.id = nonNull(geometryObjectStructure.getStructureName()) ? geometryObjectStructure.getStructureName() : "$geometry" + stringId;
                modelMesh.attributes = vertexAttributes.toArray(VertexAttribute.class);
                modelMesh.vertices = finalVertices;
                modelMesh.parts = new ModelMeshPart[]{modelMeshPart};

                modelData.addMesh(modelMesh);

                ModelNodePart modelNodePart = new ModelNodePart();
                modelNodePart.meshPartId = modelMeshPart.id;

                ModelNode node = new ModelNode();
                node.id = nonNull(geometryNodeStructure.getName()) ? geometryNodeStructure.getName() : nonNull(geometryNodeStructure.getStructureName()) ? geometryNodeStructure.getStructureName() : "$node" + stringId;
                node.meshId = geometryNodeStructure.getObjectReference().getComponent(0);
                node.parts = new ModelNodePart[]{modelNodePart};

                geometryNodeStructure.getTransform().ifPresent(transform -> {
                    node.translation = transform.getTransform().getTranslation(new Vector3());
                    node.rotation = transform.getTransform().getRotation(new Quaternion());
                    node.scale = transform.getTransform().getScale(new Vector3());
                });

                modelData.nodes.add(node);
            });

            return modelData;

        } catch (IOException e) {
            throw new GdxRuntimeException("Unable to parse Ogex Model " + fileHandle.name(), e);
        }
    }

    private int parseType(MeshType type) {
        if (type.equals(MeshType.Triangles)) {
            return GL20.GL_TRIANGLES;
        } else if (type.equals(MeshType.Lines)) {
            return GL20.GL_LINES;
        } else if (type.equals(MeshType.Points)) {
            return GL20.GL_POINTS;
        } else if (type.equals(MeshType.TriangleStrip)) {
            return GL20.GL_TRIANGLE_STRIP;
        } else if (type.equals(MeshType.LineStrip)) {
            return GL20.GL_LINE_STRIP;
        } else {
            throw new GdxRuntimeException("Unknown primitive type '" + type
                    + "', should be one of triangle, trianglestrip, line, linestrip or point");
        }
    }
}
