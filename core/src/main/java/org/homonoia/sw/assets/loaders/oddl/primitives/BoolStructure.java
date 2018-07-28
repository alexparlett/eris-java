package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.LiteralEncoding;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;

public final class BoolStructure extends PrimitiveStructure<Boolean> {
    public static final String IDENTIFIER = "bool";


    public BoolStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public BoolStructure(final String name, final int arrayLength) {
        super(BoolStructure.IDENTIFIER, name, arrayLength);
    }

    public BoolStructure(final String name) {
        super(BoolStructure.IDENTIFIER, name);
    }

    public BoolStructure(final int arrayLength) {
        super(BoolStructure.IDENTIFIER, arrayLength);
    }

    public BoolStructure() {
        super(BoolStructure.IDENTIFIER);
    }

    @Override
    protected Boolean decodeDataElement(final String token) throws OpenDDLException {
        return Boolean.valueOf(Decoder.decodeBoolean(token));
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final Boolean dataElement) {
        LiteralEncoding.encodeBoolean(stringBuilder, dataElement.booleanValue());
    }
}
