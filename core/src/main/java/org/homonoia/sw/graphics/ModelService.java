package org.homonoia.sw.graphics;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.github.czyzby.autumn.annotation.Component;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Position;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/04/2018
 */
@Component
public class ModelService {

    private ModelBuilder modelBuilder = new ModelBuilder();

    public Model createSphere(int width, int height, int depth, Material material) {
        return modelBuilder.createSphere(width, height, depth, 10, 10, material, Position | Normal);
    }


}
