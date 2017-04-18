package org.homonoia.eris.ecs;

import lombok.Getter;
import lombok.Setter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;

/**
 * Created by alexparlett on 26/05/2016.
 */
@Getter
@Setter
public abstract class Component extends Contextual {

    private Entity entity;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public Component(Context context) {
        super(context);
    }

    public void update(double delta) {}
}
