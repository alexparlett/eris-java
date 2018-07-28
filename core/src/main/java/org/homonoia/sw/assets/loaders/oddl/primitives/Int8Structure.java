package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.LiteralEncoding;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;

public final class Int8Structure extends PrimitiveStructure<Byte> implements MultipleLiteralEncodings {
    public static final String IDENTIFIER = "int8";


    private int literalEncoding = LiteralEncoding.DECIMAL;

    public Int8Structure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public Int8Structure(final String name, final int arrayLength) {
        super(Int8Structure.IDENTIFIER, name, arrayLength);
    }

    public Int8Structure(final String name) {
        super(Int8Structure.IDENTIFIER, name);
    }

    public Int8Structure(final int arrayLength) {
        super(Int8Structure.IDENTIFIER, arrayLength);
    }

    public Int8Structure() {
        super(Int8Structure.IDENTIFIER);
    }

    @Override
    protected Byte decodeDataElement(final String token) throws OpenDDLException {
        return Byte.valueOf(Decoder.decodeInt8(token));
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final Byte dataElement) {
        LiteralEncoding.encodeByte(stringBuilder, dataElement.byteValue(), literalEncoding);
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
