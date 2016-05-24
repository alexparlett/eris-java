package org.homonoia.eris.resources.types.mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class Face {

    private List<Vertex> vertices = new ArrayList<>();

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(final List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public void addVertex(final Vertex vertex) {
        Objects.requireNonNull(vertex);
        vertices.add(vertex);
    }
}
