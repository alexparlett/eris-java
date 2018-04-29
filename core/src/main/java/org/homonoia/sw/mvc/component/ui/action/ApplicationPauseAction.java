package org.homonoia.sw.mvc.component.ui.action;

import com.badlogic.gdx.Gdx;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** LML view action. Forces application's pausing on invocation.
 *
 * @author MJ */
public class ApplicationPauseAction implements ActorConsumer<Void, Object> {
    /** Name of the action as it appears in the templates. Can be changed globally before the context loading. */
    public static String ID = "app:pause";

    @Override
    public Void consume(final Object actor) {
        Gdx.app.getApplicationListener().pause();
        return null;
    }
}
