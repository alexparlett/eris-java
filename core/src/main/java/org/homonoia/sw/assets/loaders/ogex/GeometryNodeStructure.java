package org.homonoia.sw.assets.loaders.ogex;

import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;
import org.homonoia.sw.assets.loaders.oddl.primitives.Reference;

import java.util.Optional;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class GeometryNodeStructure extends NodeStructure {

    private Boolean visible;
    private Boolean shadow;
    private Boolean motionBlur;
    private String name;
    private Reference objectReference;

    public GeometryNodeStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    @Override
    protected void validate(RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        if (getStructureCount("ObjectRef") != 1) {
            throw new OpenDDLException("GeometryNodeStructure must have one ObjectRef child structure");
        }

        if (getStructureCount("Name") > 1) {
            throw new OpenDDLException("GeometryNodeStructure must have zero or one Name child structure");
        }

        if (hasProperty("visible")) {
            visible = getBooleanProperty("visible");
        }
        if (hasProperty("shadow")) {
            shadow = getBooleanProperty("shadow");
        }
        if (hasProperty("motion_blur")) {
            motionBlur = getBooleanProperty("motion_blur");
        }

        getFirstStructure("ObjectRef")
                .map(ObjectRefStructure.class::cast)
                .map(ObjectRefStructure::getReference)
                .ifPresent(reference -> objectReference = reference);

        getFirstStructure("Name")
                .map(NameStructure.class::cast)
                .map(NameStructure::getName)
                .ifPresent(s -> name = s);
    }

    public Optional<TransformStructure> getTransform() {
        return getFirstStructure("Transform").map(TransformStructure.class::cast);
    }

    public static class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new GeometryNodeStructure(identifier, decoder);
        }
    }
}
