package org.homonoia.sw.assets.loaders.ogex;

import lombok.Getter;
import org.homonoia.sw.assets.loaders.oddl.Decoder;
import org.homonoia.sw.assets.loaders.oddl.NodeStructure;
import org.homonoia.sw.assets.loaders.oddl.OpenDDLException;
import org.homonoia.sw.assets.loaders.oddl.RootStructure;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/07/2018
 */
@Getter
public class GeometryObjectStructure extends NodeStructure {

    private boolean visible = true;
    private boolean shadow = true;
    private boolean motionBlur = true;
    private Map<Long, MeshStructure> lods = new HashMap<>();

    public GeometryObjectStructure(final String identifier, final Decoder decoder) throws OpenDDLException {
        super(identifier, decoder);
    }

    @Override
    protected void validate(RootStructure rootStructure) throws OpenDDLException {
        super.validate(rootStructure);

        if (getStructureCount("Mesh") < 1) {
            throw new OpenDDLException("GeometryObjectStructure must have at least one Mesh child structure");
        }

        if (isNull(getStructureName())){
            throw new OpenDDLException("GeometryObjectStructure must have a structure name");
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

        getStructures("Mesh").forEachRemaining(structure -> {
            MeshStructure meshStructure = (MeshStructure) structure;
            lods.put(meshStructure.getLod(), meshStructure);
        });
    }

    public MeshStructure getMesh() {
        return lods.get(0L);
    }

    public static class Builder implements NodeStructure.Builder {
        @Override
        public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException {
            return new GeometryObjectStructure(identifier, decoder);
        }
    }
}
