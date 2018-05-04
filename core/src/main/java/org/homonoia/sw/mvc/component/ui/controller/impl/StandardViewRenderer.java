package org.homonoia.sw.mvc.component.ui.controller.impl;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import org.homonoia.sw.mvc.component.ui.controller.ViewRenderer;

/** Renders the view by invoking stage acting and rendering it.
 *
 * @author MJ */
public class StandardViewRenderer implements ViewRenderer {
    @Override
    public void render(final Stage stage, final float delta) {
        GdxUtilities.clearScreen();
        stage.act(delta);
        stage.draw();
    }
}
