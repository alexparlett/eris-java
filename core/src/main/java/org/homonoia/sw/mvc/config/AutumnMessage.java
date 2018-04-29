package org.homonoia.sw.mvc.config;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import org.homonoia.sw.mvc.component.asset.AssetService;
import org.homonoia.sw.mvc.component.ui.InterfaceService;

/** Contains all messages posted by Autumn MVC components using a
 * {@link com.github.czyzby.autumn.processor.event.MessageDispatcher}.
 *
 * @author MJ */
public class AutumnMessage {
    /** Can be extended to contain all application's messages, but should not be initiated. */
    protected AutumnMessage() {
    }

    /** Posted each time SOME assets scheduled with {@link AssetService}
     * are loaded. This is not posted if assets where loaded on demand with
     * {@link AssetService#finishLoading(String, Class)} or
     * {@link AssetService#finishLoading(String, Class, AssetLoaderParameters)}
     * methods. */
    public static final String ASSETS_LOADED = "AMVC_assetsLoaded";

    /** Posted when all application's {@link com.badlogic.gdx.scenes.scene2d.ui.Skin}s are fully loaded. */
    public static final String SKINS_LOADED = "AMVC_skinsLoaded";

    /** Posted when the game's window is resized. Posted AFTER
     * {@link InterfaceService} resizes current view (if any is present). */
    public static final String GAME_RESIZED = "AMVC_gameResized";

    /** Posted when the game is paused. Posted AFTER {@link InterfaceService}
     * pauses current view (if any is present). */
    public static final String GAME_PAUSED = "AMVC_gamePaused";

    /** Posted when the game is resumed. Posted AFTER {@link InterfaceService}
     * resumes current view (if any is present). */
    public static final String GAME_RESUMED = "AMVC_gameResumed";
}