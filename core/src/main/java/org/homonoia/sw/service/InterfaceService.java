package org.homonoia.sw.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.processor.event.MessageDispatcher;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import org.homonoia.sw.mvc.config.AutumnActionPriority;
import org.homonoia.sw.mvc.config.AutumnMessage;
import org.homonoia.sw.mvc.controller.ViewController;

/** Manages view controllers and a LML parser.
 *
 * @author MJ */
public class InterfaceService {
    public static float DEFAULT_FADING_TIME = 0.25f;

    private final ObjectMap<Class<?>, ViewController> controllers = GdxMaps.newObjectMap();
    private final ObjectMap<String, FileHandle> i18nBundleFiles = GdxMaps.newObjectMap();

    private final Batch batch = new SpriteBatch();

    private final ScreenSwitchingRunnable screenSwitchingRunnable = new ScreenSwitchingRunnable(this);
    private ViewController currentController;
    private boolean isControllerHiding;

    private Runnable actionOnReload;
    private Runnable actionOnShow;

    @Inject private AssetService assetService;
    @Inject private MusicService musicService;
    @Inject private MessageDispatcher messageDispatcher;
    @Inject private SkinService skinService;

    /** Allows to manually register a managed controller. For internal use mostly.
     *
     * @param mappedControllerClass class with which the controller is accessible. This does not have to be controller's
     *            actual class.
     * @param controller controller implementation, managing a single view. */
    public void registerController(final Class<?> mappedControllerClass, final ViewController controller) {
        controllers.put(mappedControllerClass, controller);
    }

    private void initiateView(final ViewController controller) {
        if (!controller.isCreated()) {
            controller.createView(this);
        }
    }

    /** @return {@link SpriteBatch} instance used to render all views. */
    public Batch getBatch() {
        return batch;
    }

    /** Hides current view (if present) and shows the view managed by the passed controller.
     *
     * @param controller class of the controller managing the view. */
    public void show(final Class<?> controller) {
        show(controllers.get(controller));
    }

    /** Hides current view (if present) and shows the view managed by the passed controller.
     *
     * @param controller class of the controller managing the view.
     * @param actionOnShow will be executed after the current screen is hidden. */
    public void show(final Class<?> controller, final Runnable actionOnShow) {
        this.actionOnShow = actionOnShow;
        show(controller);
    }

    /** Hides current view (if present) and shows the view managed by the chosen controller
     *
     * @param viewController will be set as the current view and shown. */
    public void show(final ViewController viewController) {
        if (currentController != null) {
            if (isControllerHiding) {
                switchToView(viewController);
            } else {
                hideCurrentViewAndSchedule(viewController);
            }
        } else {
            switchToView(viewController);
        }
    }

    private void switchToView(final ViewController viewController) {
        Gdx.app.postRunnable(screenSwitchingRunnable.switchToView(viewController));
    }

    private void hideCurrentViewAndSchedule(final ViewController viewController) {
        isControllerHiding = true;
        currentController.hide(Actions.sequence(hidingActionProvider.provideAction(currentController, viewController),
                Actions.run(CommonActionRunnables.getViewSetterRunnable(this, viewController))));
    }

    /** Forces eager initiation of all views managed by registered controllers. Initiates dialogs that cache and reuse
     * their dialog actor instance. */
    public void initiateAllControllers() {
        for (final ViewController controller : controllers.values()) {
            initiateView(controller);
        }
    }

    /** Hides current view, destroys all screens and shows the recreated current view. Note that it won't recreate all
     * views that were previously initiated, as views are constructed on demand.
     *
     * @see #initiateAllControllers() */
    public void reload() {
        currentController
                .hide(Actions.sequence(hidingActionProvider.provideAction(currentController, currentController),
                        Actions.run(CommonActionRunnables.getActionPosterRunnable(getViewReloadingRunnable()))));
    }

    /** Hides current view, destroys all screens and shows the recreated current view. Note that it won't recreate all
     * views that were previously initiated, as views are constructed on demand.
     *
     * @param actionOnReload will be executed after the current screen is hidden.
     * @see #initiateAllControllers() */
    public void reload(final Runnable actionOnReload) {
        this.actionOnReload = actionOnReload;
        reload();
    }

    /** Forces destruction of the selected view. The view should not be currently shown, as it still might get a render
     * call if next screen was not set.
     *
     * @param viewController will be destroyed.
     * @see #remove(Class) */
    public void destroy(final Class<?> viewController) {
        controllers.get(viewController).destroyView();
    }

    /** Forces destruction and complete removal of the controller from the service. The view should not be currently
     * shown, as it still might get a render call if next screen was not set.
     *
     * @param viewController will be destroyed and removed. Will no longer be available.
     * @see #destroy(Class) */
    public void remove(final Class<?> viewController) {
        destroy(viewController);
        controllers.remove(viewController);
    }

    private Runnable getViewReloadingRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                executeActionOnReload();
                reloadViews();
            }
        };
    }

    private void executeActionOnReload() {
        if (actionOnReload != null) {
            actionOnReload.run();
            actionOnReload = null;
        }
    }

    private void executeActionOnShow() {
        if (actionOnShow != null) {
            actionOnShow.run();
            actionOnShow = null;
        }
    }

    private void reloadViews() {
        destroyViews();
        final ViewController viewToShow = currentController;
        currentController = null;
        show(viewToShow);
    }

    /** Renders the current view, if present.
     *
     * @param delta time passed since the last update. */
    public void render(final float delta) {
        if (currentController != null) {
            currentController.render(delta);
        }
    }

    /** Resizes the current view, if present.
     *
     * @param width new width of the screen.
     * @param height new height of the screen. */
    public void resize(final int width, final int height) {
        if (currentController != null) {
            currentController.resize(width, height);
        }
        messageDispatcher.postMessage(AutumnMessage.GAME_RESIZED);
    }

    /** Pauses the current view, if present. */
    public void pause() {
        if (currentController != null) {
            currentController.pause();
        }
        messageDispatcher.postMessage(AutumnMessage.GAME_PAUSED);
    }

    /** Resumes the current view, if present. */
    public void resume() {
        if (currentController != null) {
            currentController.resume();
        }
        messageDispatcher.postMessage(AutumnMessage.GAME_RESUMED);
    }

    /** @return controller of currently shown view. Might be null. Mostly for internal use. */
    public ViewController getCurrentController() {
        return currentController;
    }

    /** @param forClass class associated with the controller. Does not have to be a
     *            {@link ViewController} - can be a wrapped by an
     *            auto-generated controller instance.
     * @return instance of the passed class or a controller wrapping the selected class. Can be null. */
    public ViewController getController(final Class<?> forClass) {
        return controllers.get(forClass);
    }

    @Destroy(priority = AutumnActionPriority.LOW_PRIORITY)
    private void dispose() {
        destroyViews();
        controllers.clear();
        batch.dispose();
    }

    private void destroyViews() {
        for (final ViewController controller : controllers.values()) {
            controller.destroyView();
        }
    }

    /** @return an array containing all managed controllers. Note that this is not used by the service internally and
     *         can be safely modified. */
    public Array<ViewController> getControllers() {
        return GdxArrays.newArray(controllers.values());
    }

    /** Avoids anonymous classes.
     *
     * @author MJ */
    private static class ScreenSwitchingRunnable implements Runnable {
        private final InterfaceService interfaceService;
        private ViewController controllerToShow;

        public ScreenSwitchingRunnable(final InterfaceService interfaceService) {
            this.interfaceService = interfaceService;
        }

        public Runnable switchToView(final ViewController controllerToShow) {
            interfaceService.executeActionOnShow();
            this.controllerToShow = controllerToShow;
            return this;
        }

        @Override
        public void run() {
            interfaceService.isControllerHiding = false;
            interfaceService.currentController = controllerToShow;
            interfaceService.initiateView(controllerToShow);
            controllerToShow.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            interfaceService.currentController.show(Actions.sequence());
            controllerToShow = null;
        }
    }
}