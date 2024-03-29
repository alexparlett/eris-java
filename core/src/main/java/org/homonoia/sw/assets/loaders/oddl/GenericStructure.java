package org.homonoia.sw.assets.loaders.oddl;

public class GenericStructure extends NodeStructure {
    public GenericStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }


    public GenericStructure(final String identifier) {
        super(identifier);
    }

    public static class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new GenericStructure(identifier, decoder);
        }
    }
}
