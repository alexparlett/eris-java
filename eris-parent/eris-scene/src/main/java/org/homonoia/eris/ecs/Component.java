package org.homonoia.eris.ecs;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alexparlett on 26/05/2016.
 */
@Getter
@Setter
public abstract class Component {

    private Entity entity;

}
