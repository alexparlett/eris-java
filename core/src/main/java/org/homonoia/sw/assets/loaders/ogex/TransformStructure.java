package org.homonoia.sw.assets.loaders.ogex;

import com.badlogic.gdx.math.Matrix4;
import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;
import org.homonoia.sw.assets.loaders.oddl.Structure;
import org.homonoia.sw.assets.loaders.oddl.primitives.FloatStructure;

import static net.dermetfan.gdx.utils.ArrayUtils.unbox;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class TransformStructure extends NodeStructure {

    private Matrix4 transform;

    public TransformStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    @Override
    protected void validate(RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        if (getStructureCount() > 1) {
            throw new OpenDDLException("TransformStructure must only have one child structure");
        }

        final Structure structure = getStructure(0);

        if (!(structure instanceof FloatStructure)) {
            throw new OpenDDLException("TransformStructure must contain a FloatStructure");
        }

        final FloatStructure floatStructure = (FloatStructure) structure;

        if (floatStructure.getArrayLength() != 16 || floatStructure.getDataElementCount() != 16) {
            throw new OpenDDLException("TransformStructure's FloatStructure child must be an array structure and must contain 16 data elements");
        }

        final float[] floats = unbox(floatStructure.getDataElements().toArray(new Float[floatStructure.getDataElementCount()]));
        transform = new Matrix4(floats);
    }

    public static class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new TransformStructure(identifier, decoder);
        }
    }
}
