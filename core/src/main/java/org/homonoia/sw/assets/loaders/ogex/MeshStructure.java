package org.homonoia.sw.assets.loaders.ogex;

import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;
import org.homonoia.sw.assets.loaders.oddl.Structure;

import java.util.Iterator;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class MeshStructure extends NodeStructure {

    private MeshType primitive;
    private Long lod;
    private Integer numberOfVertices;
    private int vertexSize = 0;
    private Array<VertexArrayStructure> vertexArrays;

    public MeshStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    @Override
    protected void validate(RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        Iterator<Structure> vertexArrayIterator = getStructures("VertexArray");
        int structureCount = getStructureCount("VertexArray");
        if (structureCount < 1) {
            throw new OpenDDLException("MeshStructure must have at least one VertexArray child structure");
        }

        primitive = MeshType.lookup(getStringProperty("primitive"));

        if (hasProperty("lod")) {
            lod = getUnsignedInt32Property("lod");
        } else {
            lod = 0L;
        }

        vertexArrays = new Array<>(structureCount);

        vertexArrayIterator.forEachRemaining(structure -> {
            VertexArrayStructure vertexArrayStructure = (VertexArrayStructure) structure;
            if (isNull(numberOfVertices)) {
                numberOfVertices = vertexArrayStructure.getVertices().size;
            } else if (numberOfVertices != vertexArrayStructure.getVertices().size) {
                throw new OpenDDLException("MeshStructure's VertexArrays must all have the same number of vertices");
            }
            vertexSize += vertexArrayStructure.getVertexSize();

            vertexArrays.add(vertexArrayStructure);
        });

    }

    public Optional<IndexArrayStructure> getIndexArray() {
        return getFirstStructure("IndexArray").map(IndexArrayStructure.class::cast);
    }

    public static class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new MeshStructure(identifier, decoder);
        }
    }
}
