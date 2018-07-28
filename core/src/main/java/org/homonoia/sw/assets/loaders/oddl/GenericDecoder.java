package org.homonoia.sw.assets.loaders.oddl;

public class GenericDecoder extends Decoder {
    public GenericDecoder(final String openddlString) {
        super(openddlString);

        addBuilder(Decoder.WILDCARD_IDENTIFIER, new GenericStructure.Builder());
    }
}
