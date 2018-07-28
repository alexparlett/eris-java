package org.homonoia.sw.assets.loaders.ogex;

import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.PrimitiveStructure;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;
import org.homonoia.sw.assets.loaders.oddl.Structure;
import org.homonoia.sw.assets.loaders.oddl.primitives.FloatStructure;
import org.homonoia.sw.assets.loaders.oddl.primitives.StringStructure;
import org.homonoia.sw.assets.loaders.ogex.metrics.FloatMetric;
import org.homonoia.sw.assets.loaders.ogex.metrics.Metric;
import org.homonoia.sw.assets.loaders.ogex.metrics.StringMetric;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class MetricStructure extends NodeStructure {

    private String name;
    private Metric metric;

    public MetricStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    public FloatMetric getFloat() {
        return (FloatMetric) metric;
    }

    public StringMetric getString() {
        return (StringMetric) metric;
    }

    @Override
    protected void validate(RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        if (getStructureCount() > 1) {
            throw new OpenDDLException("MetricStructure must only have one child structure");
        }

        final Structure structure = getStructure(0);

        name = getStringProperty("key");

        if (structure instanceof FloatStructure) {
            FloatStructure floatStructure = (FloatStructure) structure;

            if (floatStructure.getArrayLength() != PrimitiveStructure.NOT_AN_ARRAY || floatStructure.getDataElementCount() != 1) {
                throw new OpenDDLException("MetricStructure's FloatStructure child must not be an array structure and must contain one data element");
            }

            metric = new FloatMetric(floatStructure.getDataElement(0));
        } else if (structure instanceof StringStructure) {
            StringStructure stringStructure = (StringStructure) structure;

            if (stringStructure.getArrayLength() != PrimitiveStructure.NOT_AN_ARRAY || stringStructure.getDataElementCount() != 1) {
                throw new OpenDDLException("MetricStructure's StringStructure child must not be an array structure and must contain one data element");
            }

            metric = new StringMetric(stringStructure.getDataElement(0));
        }
    }

    public static class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new MetricStructure(identifier, decoder);
        }
    }
}
