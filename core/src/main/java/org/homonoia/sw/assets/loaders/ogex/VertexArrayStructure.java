package org.homonoia.sw.assets.loaders.ogex;

import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;
import org.homonoia.sw.assets.loaders.oddl.Structure;
import org.homonoia.sw.assets.loaders.oddl.primitives.FloatStructure;

import java.util.List;

import static net.dermetfan.utils.ArrayUtils.unbox;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class VertexArrayStructure extends NodeStructure {

    private Array<float[]> vertices;
    private int vertexSize;
    private String attribute;
    private int index;

    public VertexArrayStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    @Override
    protected void validate(RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        if (getStructureCount() > 1) {
            throw new OpenDDLException("VertexArrayStructure must only have one child structure");
        }

        final Structure structure = getStructure(0);

        if (!(structure instanceof FloatStructure)) {
            throw new OpenDDLException("VertexArrayStructure must contain a FloatStructure");
        }

        final FloatStructure floatStructure = (FloatStructure) structure;

        if (floatStructure.getDataElementCount() < floatStructure.getArrayLength()) {
            throw new OpenDDLException("VertexArrayStructure's FloatStructure child must be an array structure and must contain at least the array length of elements");
        }

        vertices = new Array<>(floatStructure.getArrayCount());
        for (int i = 0; i < floatStructure.getArrayCount(); i++) {
            List<Float> array = floatStructure.getArray(i);
            vertices.add(unbox(array.toArray(new Float[floatStructure.getArrayLength()])));
        }

        vertexSize = floatStructure.getArrayLength();

        String attrib = getStringProperty("attrib");
        String[] split = attrib.split("(\\[|\\])");
        if (split.length == 2) {
            attribute = split[0];
            index = Integer.parseInt(split[1]);
        } else if (split.length == 1) {
            attribute = split[0];
        } else {
            throw new OpenDDLException("VertexArrayStructure's attrib property must exist and be in format of name or name[index]");
        }
    }

    public static class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new VertexArrayStructure(identifier, decoder);
        }
    }
}
