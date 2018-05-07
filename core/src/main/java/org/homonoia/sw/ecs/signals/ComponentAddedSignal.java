package org.homonoia.sw.ecs.signals;

import lombok.Builder;
import lombok.Data;
import org.homonoia.sw.ecs.core.Entity;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 05/05/2018
 */
@Data
@Builder
public class ComponentAddedSignal implements Signal<Entity> {
    private Entity item;
}
