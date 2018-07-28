package org.homonoia.sw.assets.loaders.oddl.primitives;

import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.LiteralEncoding;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;

public final class FloatStructure extends PrimitiveStructure<Float> implements MultipleLiteralEncodings {
    public static final String IDENTIFIER = "float";


    private int literalEncoding = LiteralEncoding.FLOATING_POINT;
    ;

    public FloatStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public FloatStructure(final String name, final int arrayLength) {
        super(FloatStructure.IDENTIFIER, name, arrayLength);
    }

    public FloatStructure(final String name) {
        super(FloatStructure.IDENTIFIER, name);
    }

    public FloatStructure(final int arrayLength) {
        super(FloatStructure.IDENTIFIER, arrayLength);
    }

    public FloatStructure() {
        super(FloatStructure.IDENTIFIER);
    }

    @Override
    protected Float decodeDataElement(final String token) throws OpenDDLException {
        return Float.valueOf(Decoder.decodeFloat(token));
    }

    @Override
    protected void encodeDataElement(final StringBuilder stringBuilder, final Float dataElement) {
        LiteralEncoding.encodeFloat(stringBuilder, dataElement.floatValue(), literalEncoding);
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
