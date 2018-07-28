package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.LiteralEncoding;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;

public final class Int16Structure extends PrimitiveStructure<Short> implements MultipleLiteralEncodings {
    public static final String IDENTIFIER = "int16";


    private int literalEncoding = LiteralEncoding.DECIMAL;

    public Int16Structure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public Int16Structure(final String name, final int arrayLength) {
        super(Int16Structure.IDENTIFIER, name, arrayLength);
    }

    public Int16Structure(final String name) {
        super(Int16Structure.IDENTIFIER, name);
    }

    public Int16Structure(final int arrayLength) {
        super(Int16Structure.IDENTIFIER, arrayLength);
    }

    public Int16Structure() {
        super(Int16Structure.IDENTIFIER);
    }

    @Override
    protected Short decodeDataElement(final String token) throws OpenDDLException {
        return Short.valueOf(Decoder.decodeInt16(token));
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final Short dataElement) {
        LiteralEncoding.encodeShort(stringBuilder, dataElement.shortValue(), literalEncoding);
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
