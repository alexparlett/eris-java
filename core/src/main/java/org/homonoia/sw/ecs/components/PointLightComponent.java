package org.homonoia.sw.ecs.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.homonoia.sw.ecs.core.Component;
import org.homonoia.sw.ecs.core.DefaultConstructor;
import org.homonoia.sw.ecs.core.Entity;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 07/05/2018
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PointLightComponent extends Component {
    private PointLight pointLight;
    private Environment environment;

    @DefaultConstructor
    public PointLightComponent(final Color color, final Vector3 position, final float intensity, final Environment environment) {
        this.environment = environment;
        pointLight = new PointLight().set(color, position, intensity);
    }

    @Override
    protected void addedToEntity(Entity entity) {
        environment.add(pointLight);
    }

    @Override
    protected void removedFromEntity() {
        environment.remove(pointLight);
    }
}
