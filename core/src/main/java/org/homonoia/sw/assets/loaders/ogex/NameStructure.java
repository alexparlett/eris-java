package org.homonoia.sw.assets.loaders.ogex;

import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;
import org.homonoia.sw.assets.loaders.oddl.Structure;
import org.homonoia.sw.assets.loaders.oddl.primitives.StringStructure;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class NameStructure extends NodeStructure {

    private String name;

    public NameStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    @Override
    protected void validate(final RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        if (getStructureCount() > 1) {
            throw new OpenDDLException("NameStructure must only have one child structure");
        }

        final Structure nameStructure = getStructure(0);

        if (!(nameStructure instanceof StringStructure)) {
            throw new OpenDDLException("NameStructure must contain a StringStructure");
        }

        final StringStructure nameStringStructure = (StringStructure) nameStructure;

        if (nameStringStructure.getArrayLength() != PrimitiveStructure.NOT_AN_ARRAY || nameStringStructure.getDataElementCount() != 1) {
            throw new OpenDDLException("NameStructure's StringStructure child must not be an array structure and must contain one data element");
        }

        name = nameStringStructure.getDataElement(0);
    }

    public static final class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new NameStructure(identifier, decoder);
        }
    }
}
