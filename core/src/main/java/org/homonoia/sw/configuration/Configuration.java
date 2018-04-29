package org.homonoia.sw.configuration;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Initiate;
import org.homonoia.sw.mvc.component.ui.SkinService;
import org.homonoia.sw.mvc.stereotype.preference.AvailableLocales;
import org.homonoia.sw.mvc.stereotype.preference.I18nBundle;
import org.homonoia.sw.mvc.stereotype.preference.I18nLocale;
import org.homonoia.sw.mvc.stereotype.preference.LmlMacro;
import org.homonoia.sw.mvc.stereotype.preference.LmlParserSyntax;
import org.homonoia.sw.mvc.stereotype.preference.Preference;
import org.homonoia.sw.mvc.stereotype.preference.StageViewport;
import org.homonoia.sw.mvc.stereotype.preference.sfx.MusicEnabled;
import org.homonoia.sw.mvc.stereotype.preference.sfx.MusicVolume;
import org.homonoia.sw.mvc.stereotype.preference.sfx.SoundEnabled;
import org.homonoia.sw.mvc.stereotype.preference.sfx.SoundVolume;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.util.Lml;
import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;
import com.kotcrab.vis.ui.VisUI;
import org.homonoia.sw.SolarianWarsGame;
import org.homonoia.sw.service.ScaleService;

/**
 * Thanks to the Component annotation, this class will be automatically found and processed.
 * <p>
 * This is a utility class that configures application settings.
 */
@Component
public class Configuration {
    /**
     * Name of the application's preferences file.
     */
    public static final String PREFERENCES = "SolarianWars";
    /**
     * Path to the internationalization bundle.
     */
    @I18nBundle
    private final String bundlePath = "i18n/bundle";
    /**
     * Enabling VisUI usage.
     */
    @LmlParserSyntax
    private final LmlSyntax syntax = new VisLmlSyntax();
    /**
     * Parsing macros available in all views.
     */
    @LmlMacro
    private final String globalMacro = "ui/templates/macros/global.lml";
    /**
     * Using a custom viewport provider - Autumn MVC defaults to the ScreenViewport, as it is the only viewport that
     * doesn't need to know application's targeted screen size. This provider overrides that by using more sophisticated
     * FitViewport that works on virtual units rather than pixels.
     */
    @StageViewport
    private final ObjectProvider<Viewport> viewportProvider = new ObjectProvider<Viewport>() {
        @Override
        public Viewport provide() {
            return new FitViewport(SolarianWarsGame.WIDTH, SolarianWarsGame.HEIGHT);
        }
    };

    /**
     * These sound-related fields allow MusicService to store settings in preferences file. Sound preferences will be
     * automatically saved when the application closes and restored the next time it's turned on. Sound-related methods
     * methods will be automatically added to LML templates - see settings.lml template.
     */
    @SoundVolume(preferences = PREFERENCES)
    private final String soundVolume = "soundVolume";
    @SoundEnabled(preferences = PREFERENCES)
    private final String soundEnabled = "soundOn";
    @MusicVolume(preferences = PREFERENCES)
    private final String musicVolume = "musicVolume";
    @MusicEnabled(preferences = PREFERENCES)
    private final String musicEnabledPreference = "musicOn";

    /**
     * These i18n-related fields will allow LocaleService to save game's locale in preferences file. Locale changing
     * actions will be automatically added to LML templates - see settings.lml template.
     */
    @I18nLocale(propertiesPath = PREFERENCES, defaultLocale = "en")
    private final String localePreference = "locale";
    @AvailableLocales
    private final String[] availableLocales = new String[]{"en"};

    /**
     * Setting the default Preferences object path.
     */
    @Preference
    private final String preferencesPath = PREFERENCES;

    /**
     * Thanks to the Initiate annotation, this method will be automatically invoked during context building. All
     * method's parameters will be injected with values from the context.
     *
     * @param scaleService contains current GUI scale.
     * @param skinService  contains GUI skin.
     */
    @Initiate
    public void initiateConfiguration(final ScaleService scaleService, final SkinService skinService) {
        // Loading default VisUI skin with the selected scale:
        VisUI.load(scaleService.getScale());
        // Registering VisUI skin with "default" name - this skin will be the default one for all LML widgets:
        skinService.addSkin("default", VisUI.getSkin());
        // Thanks to this setting, only methods annotated with @LmlAction will be available in views, significantly
        // speeding up method look-up:
        Lml.EXTRACT_UNANNOTATED_METHODS = false;
    }
}