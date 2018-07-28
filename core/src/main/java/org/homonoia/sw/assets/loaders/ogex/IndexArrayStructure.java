package org.homonoia.sw.assets.loaders.ogex;

import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;
import org.homonoia.sw.assets.loaders.oddl.Structure;
import org.homonoia.sw.assets.loaders.oddl.primitives.UnsignedInt32Structure;

import java.util.List;

import static org.homonoia.sw.collections.ArrayUtils.unbox;


/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class IndexArrayStructure extends NodeStructure {

    private Array<long[]> indicies;
    private int indexSize;

    public IndexArrayStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    @Override
    protected void validate(RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        if (getStructureCount() > 1) {
            throw new OpenDDLException("IndexArrayStructure must only have one child structure");
        }

        final Structure structure = getStructure(0);

        if (!(structure instanceof UnsignedInt32Structure)) {
            throw new OpenDDLException("IndexArrayStructure must contain a UnsignedInt32Structure");
        }

        final UnsignedInt32Structure unsignedInt32Structure = (UnsignedInt32Structure) structure;

        if (unsignedInt32Structure.getDataElementCount() < unsignedInt32Structure.getArrayLength()) {
            throw new OpenDDLException("IndexArrayStructure's UnsignedInt32Structure child must be an array structure and must contain at least the array length of elements");
        }

        indicies = new Array<>(unsignedInt32Structure.getArrayCount());
        for (int i = 0; i < unsignedInt32Structure.getArrayCount(); i++) {
            List<Long> array = unsignedInt32Structure.getArray(i);
            indicies.add(unbox(array.toArray(new Long[unsignedInt32Structure.getArrayLength()])));
        }
        indexSize = unsignedInt32Structure.getArrayLength();
    }

    public static class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new IndexArrayStructure(identifier, decoder);
        }
    }
}
