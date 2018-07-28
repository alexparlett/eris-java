package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.LiteralEncoding;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;

public final class DoubleStructure extends PrimitiveStructure<Double> implements MultipleLiteralEncodings {
    public static final String IDENTIFIER = "double";


    private int literalEncoding = LiteralEncoding.FLOATING_POINT;
    ;

    public DoubleStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public DoubleStructure(final String name, final int arrayLength) {
        super(DoubleStructure.IDENTIFIER, name, arrayLength);
    }

    public DoubleStructure(final String name) {
        super(DoubleStructure.IDENTIFIER, name);
    }

    public DoubleStructure(final int arrayLength) {
        super(DoubleStructure.IDENTIFIER, arrayLength);
    }

    public DoubleStructure() {
        super(DoubleStructure.IDENTIFIER);
    }

    @Override
    protected Double decodeDataElement(final String token) throws OpenDDLException {
        return Double.valueOf(Decoder.decodeDouble(token));
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final Double dataElement) {
        LiteralEncoding.encodeDouble(stringBuilder, dataElement.doubleValue(), literalEncoding);
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
