package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.LiteralEncoding;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;

import java.math.BigInteger;

public final class UnsignedInt64Structure extends PrimitiveStructure<BigInteger> implements MultipleLiteralEncodings {
    public static final String IDENTIFIER = "unsigned_int64";

    public static final BigInteger MAX = new BigInteger("18446744073709551615");


    private int literalEncoding = LiteralEncoding.DECIMAL;

    public UnsignedInt64Structure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public UnsignedInt64Structure(final String name, final int arrayLength) {
        super(UnsignedInt64Structure.IDENTIFIER, name, arrayLength);
    }

    public UnsignedInt64Structure(final String name) {
        super(UnsignedInt64Structure.IDENTIFIER, name);
    }

    public UnsignedInt64Structure(final int arrayLength) {
        super(UnsignedInt64Structure.IDENTIFIER, arrayLength);
    }

    public UnsignedInt64Structure() {
        super(UnsignedInt64Structure.IDENTIFIER);
    }

    @Override
    protected BigInteger decodeDataElement(final String token) throws OpenDDLException {
        return Decoder.decodeUnsignedInt64(token);
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final BigInteger dataElement) {
        LiteralEncoding.encodeBigInteger(stringBuilder, dataElement, literalEncoding);
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
