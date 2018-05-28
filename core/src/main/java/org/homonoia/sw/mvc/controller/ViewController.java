package org.homonoia.sw.mvc.controller;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.homonoia.sw.service.InterfaceService;

/** Manages a single view.
 *
 * @author MJ */
public interface ViewController {
    /** Should fully view's object. This generally invokes filling {@link Stage}. This action might be called
     * multiple times, as screens are sometimes reloaded.
     *
     * @param interfaceService initiates view creation */
    void createView(InterfaceService interfaceService);

    /** Should destroy the manage view. Note that this action might be called multiple times, as screens are sometimes
     * reloaded - be careful with asset unloading. */
    void destroyView();

    /** @return true if the view was constructed with {@link #createView(InterfaceService)} method and is ready to be
     *         shown. */
    boolean isCreated();

    /** Draws the view.
     *
     * @param delta time passed since the last update. */
    void render(float delta);

    /** Resizes the managed view.
     *
     * @param width new screen width.
     * @param height new screen height. */
    void resize(int width, int height);

    /** Pauses the view. */
    void pause();

    /** Resumes the view. */
    void resume();

    /** Shows the view.
     *
     * @param action provided by the view's manager. Should be executed to show the view, as it might contain chained
     *            actions. */
    void show(Action action);

    /** Hides the view.
     *
     * @param action provided by the view's manager. Should be executed to hide the view, as it might contain chained
     *            actions. */
    void hide(Action action);

    /** @return Scene2D stage managed by the controller. */
    Stage getStage();

    /** @return if the controller is an action container (or contains one), this will be the name of the container
     *         recognized by the view. This is also the name of the view used for screen transition from within the LML
     *         templates. */
    String getViewId();

    /** @return music themes played during the view is shown. */
    Array<Music> getThemes();

    /** @return next screen's theme, according to the chosen theme ordering. */
    Music getNextTheme();
}
