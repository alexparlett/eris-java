package org.homonoia.sw.mvc.component.ui.controller.impl;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.kiwi.util.gdx.viewport.LetterboxingViewport;
import org.homonoia.sw.mvc.component.ui.controller.ViewResizer;

/** Resizes the screen by updating stage viewport.
 *
 * @author MJ */
public class StandardViewResizer implements ViewResizer {
    @Override
    public void resize(final Stage stage, final int width, final int height) {
        final Viewport viewport = stage.getViewport();
        final boolean centerCamera = viewport instanceof ScreenViewport || viewport instanceof LetterboxingViewport;
        viewport.update(width, height, centerCamera);
    }
}
