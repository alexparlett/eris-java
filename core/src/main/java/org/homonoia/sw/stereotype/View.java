package org.homonoia.sw.stereotype;

import org.homonoia.sw.mvc.controller.ViewController;
import org.homonoia.sw.mvc.dto.ThemeOrdering;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should annotate classes that manage a single view. {@link ViewController} interface.
 *
 * @author MJ
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface View {
    /**
     * @return ID of the screen used for screen transitions from within views.
     */
    String id() default "default";

    /**
     * @return list of paths to music files played while the screen is shown. By default, themes are chosen at random;
     * if there are multiple themes, there is no possibility of one theme played twice in a row. Ordering can be
     * changed with {@link View#themeOrdering()}. When view is changed to another, current theme: a) slowly
     * lowers its volume and another theme is played, b) continues to play if it is also among available themes
     * for the next view. Note that this behavior might change if the default screen transition actions are
     * changed.
     */
    String[] themes() default {};

    /**
     * @return if true, music themes are loaded at once, when the controller is constructed. If false, music themes
     * will be scheduled to be loaded and injected when the loading is finished. Defaults to false.
     */
    boolean loadThemesEagerly() default false;

    /**
     * @return determines the way next theme is determined. Defaults to random.
     */
    ThemeOrdering themeOrdering() default ThemeOrdering.RANDOM;
}