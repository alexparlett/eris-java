package org.homonoia.sw.assets.loaders.ogex;

import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;
import org.homonoia.sw.assets.loaders.oddl.Structure;
import org.homonoia.sw.assets.loaders.oddl.primitives.Reference;
import org.homonoia.sw.assets.loaders.oddl.primitives.ReferenceStructure;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class ObjectRefStructure extends NodeStructure {

    private Reference reference;

    public ObjectRefStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    @Override
    protected void validate(final RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        if (getStructureCount() > 1) {
            throw new OpenDDLException("ObjectRefStructure must only have one child structure");
        }

        final Structure refStructure = getStructure(0);

        if (!(refStructure instanceof ReferenceStructure)) {
            throw new OpenDDLException("ObjectRefStructure must contain a ReferenceStructure");
        }

        final ReferenceStructure referenceStructure = (ReferenceStructure) refStructure;

        if (referenceStructure.getArrayLength() != PrimitiveStructure.NOT_AN_ARRAY || referenceStructure.getDataElementCount() != 1) {
            throw new OpenDDLException("ObjectRefStructure's ReferenceStructure child must not be an array structure and must contain one data element");
        }

        reference = referenceStructure.getDataElement(0);
    }

    public static final class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new ObjectRefStructure(identifier, decoder);
        }
    }
}
