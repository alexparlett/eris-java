package org.homonoia.eris.ecs.components;

import lombok.Getter;
import lombok.Setter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.annotations.Requires;
import org.homonoia.eris.ecs.exceptions.MissingRequiredComponentException;
import org.ode4j.math.DQuaternion;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.internal.DxBody;
import org.ode4j.ode.internal.DxWorld;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 21/04/2017
 */
@Requires(classes = Transform.class)
@Getter
@Setter
public class RigidBody extends Component {

    private Transform transform;
    private DBody dBody;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public RigidBody(Context context) {
        super(context);
    }

    public void create(DxWorld world) {
        if (nonNull(dBody)) {
            return;
        }

        if (isNull(transform)) {
            transform = getEntity().get(Transform.class).orElseThrow(() -> new MissingRequiredComponentException(Transform.class, getEntity(), this));
        }

        DxBody dBody = DxBody.dBodyCreate(world);
        dBody.setMovedCallback(this::handleMoved);
        dBody.setPosition(transform.getTranslation().x(), transform.getTranslation().y(), transform.getTranslation().z());
        dBody.setQuaternion(new DQuaternion(transform.getRotation().x(), transform.getRotation().y(), transform.getRotation().z(), transform.getRotation().w()));
    }

    @Override
    public void destroy() {
        if (nonNull(dBody)) {
            dBody.destroy();
            dBody = null;
        }
    }

    private void handleMoved(DBody dBody) {
        if (isNull(transform)) {
            transform = getEntity().get(Transform.class).orElseThrow(() -> new MissingRequiredComponentException(Transform.class, getEntity(), this));
        }

        DVector3C position = dBody.getPosition();
        DQuaternionC rotation = dBody.getQuaternion();
        transform.rotation((float) rotation.get0(), (float) rotation.get1(), (float) rotation.get2(), (float) rotation.get3());
        transform.translation((float) position.get0(), (float) position.get1(), (float) position.get2());
    }
}
