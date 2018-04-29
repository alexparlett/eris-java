package org.homonoia.sw.mvc.config;

import org.homonoia.sw.mvc.component.asset.AssetService;
import org.homonoia.sw.mvc.component.preferences.PreferencesService;
import org.homonoia.sw.mvc.component.sfx.MusicService;
import org.homonoia.sw.mvc.component.ui.InterfaceService;
import org.homonoia.sw.mvc.component.ui.SkinService;
import org.homonoia.sw.mvc.component.ui.processor.LmlMacroAnnotationProcessor;

/** Contains priorities used by initiation and destruction methods in Autumn MVC.
 *
 * @author MJ */
public class AutumnActionPriority {
    /** Can be extended to contain all application's priorities, but should not be initiated. */
    protected AutumnActionPriority() {
    }

    /** 3. Executes first. Used by: {@link SkinService} (skins initiation and
     * assignment). */
    public static final int TOP_PRIORITY = 3;
    /** 2. Used by: {@link InterfaceService} (bundles and preferences
     * assignment, LML parser creation). */
    public static final int VERY_HIGH_PRIORITY = 2;
    /** 1. Used by: {@link PreferencesService} (preferences loading),
     * {@link LmlMacroAnnotationProcessor} (macros loading). */
    public static final int HIGH_PRIORITY = 1;
    /** 0. Used by: {@link MusicService} (adding sound settings actions to
     * LML parser). */
    public static final int DEFAULT_PRIORITY = 0;
    /** -1. Used by: {@link InterfaceService} (controllers destruction, batch
     * disposing, parser destruction). */
    public static final int LOW_PRIORITY = -1;
    /** -2. Used by: {@link MusicService} (settings saving upon destruction),
     * {@link SkinService} (skin disposing). */
    public static final int VERY_LOW_PRIORITY = -2;
    /** -3. Executes last. Used by: {@link InterfaceService} (first view
     * initiation and showing); {@link AssetService} (assets disposing),
     * {@link PreferencesService} (saving preferences) */
    public static final int MIN_PRIORITY = -3;
}