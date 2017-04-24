package core.ships;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.ecs.Component;
import org.homonoia.eris.ecs.components.RigidBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fighter extends Component {

    private static final Logger LOG = LoggerFactory.getLogger(Fighter.class);

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public Fighter(Context context) {
        super(context);
    }

    @Override
    public void update(double delta) {
        RigidBody rigidBody = (RigidBody) getEntity().get(RigidBody.class).get();
        rigidBody.getDBody().addRelForce(0,0,-1);
    }
}