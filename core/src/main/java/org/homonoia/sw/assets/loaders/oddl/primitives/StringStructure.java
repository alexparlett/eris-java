package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.*;

public final class StringStructure extends PrimitiveStructure<String> {
    public static final String IDENTIFIER = "string";


    public StringStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public StringStructure(final String name, final int arrayLength) {
        super(StringStructure.IDENTIFIER, name, arrayLength);
    }

    public StringStructure(final String name) {
        super(StringStructure.IDENTIFIER, name);
    }

    public StringStructure(final int arrayLength) {
        super(StringStructure.IDENTIFIER, arrayLength);
    }

    public StringStructure() {
        super(StringStructure.IDENTIFIER);
    }

    @Override
    protected String decodeDataElement(final String token) throws OpenDDLException {
        return Decoder.decodeString(token);
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final String dataElement) {
        LiteralEncoding.encodeString(stringBuilder, dataElement);
    }
}
