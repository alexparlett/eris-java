package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;

public class ReferenceStructure extends PrimitiveStructure<Reference> {
    public static final String IDENTIFIER = "ref";


    public ReferenceStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public ReferenceStructure(final String name, final int arrayLength) {
        super(ReferenceStructure.IDENTIFIER, name, arrayLength);
    }

    public ReferenceStructure(final String name) {
        super(ReferenceStructure.IDENTIFIER, name);
    }

    public ReferenceStructure(final int arrayLength) {
        super(ReferenceStructure.IDENTIFIER, arrayLength);
    }

    public ReferenceStructure() {
        super(ReferenceStructure.IDENTIFIER);
    }

    @Override
    protected Reference decodeDataElement(final String token) throws OpenDDLException {
        return new Reference(token);
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final Reference dataElement) {
        stringBuilder.append(dataElement.toString());
    }

    @Override
    protected void validate(final RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        for (final Reference reference : this) {
            if (reference.isGlobal()) {
                if (rootStructure.getStructure(reference) == null) {
                    throw new OpenDDLException("Unable to resolve global reference " + reference);
                }
            } else {
                if (getParentStructure().getStructure(reference) == null) {
                    throw new OpenDDLException("Unable to resolve local reference " + reference);
                }
            }
        }
    }
}
