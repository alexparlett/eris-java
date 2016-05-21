package org.homonoia.eris.resources.types.mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class Face {

    private List<Integer> indicies = new ArrayList<>();
    private List<Integer> textureCoordsIds = new ArrayList<>();
    private List<Integer> normalIds = new ArrayList<>();

    public List<Integer> getIndicies() {
        return indicies;
    }

    public void setIndicies(final List<Integer> indicies) {
        this.indicies = indicies;
    }

    public List<Integer> getTextureCoordsIds() {
        return textureCoordsIds;
    }

    public void setTextureCoordsIds(final List<Integer> textureCoordsIds) {
        this.textureCoordsIds = textureCoordsIds;
    }

    public List<Integer> getNormalIds() {
        return normalIds;
    }

    public void setNormalIds(final List<Integer> normalIds) {
        this.normalIds = normalIds;
    }
}
