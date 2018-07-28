package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.LiteralEncoding;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;

public final class Int32Structure extends PrimitiveStructure<Integer> implements MultipleLiteralEncodings {
    public static final String IDENTIFIER = "int32";


    private int literalEncoding = LiteralEncoding.DECIMAL;

    public Int32Structure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public Int32Structure(final String name, final int arrayLength) {
        super(Int32Structure.IDENTIFIER, name, arrayLength);
    }

    public Int32Structure(final String name) {
        super(Int32Structure.IDENTIFIER, name);
    }

    public Int32Structure(final int arrayLength) {
        super(Int32Structure.IDENTIFIER, arrayLength);
    }

    public Int32Structure() {
        super(Int32Structure.IDENTIFIER);
    }

    @Override
    protected Integer decodeDataElement(final String token) throws OpenDDLException {
        return Integer.valueOf(Decoder.decodeInt32(token));
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final Integer dataElement) {
        LiteralEncoding.encodeInt(stringBuilder, dataElement.intValue(), literalEncoding);
    }

    @Override
    public int getLiteralEncoding() {
        return literalEncoding;
    }

    @Override
    public void setLiteralEncoding(final int literalEncoding) {
        this.literalEncoding = literalEncoding;
    }
}
