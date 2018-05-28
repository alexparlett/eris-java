package org.homonoia.sw.service;

import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Inject;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;

/**
 * This service handles GUI scale.
 */
@Component
public class ScaleService {
    // @Inject-annotated fields will be automatically filled by the context initializer.
    @Inject
    private InterfaceService interfaceService;
    @Inject
    private SkinService skinService;

    private SkinScale skinScale;

    /**
     * @return current GUI scale.
     */
    public SkinScale getScale() {
        return skinScale;
    }

    /**
     * @return all scales supported by the application.
     */
    public SkinScale[] getScales() {
        return SkinScale.values();
    }

    /**
     * @param scale the new application's scale.
     */
    public void changeScale(final SkinScale scale) {
        if (skinScale == scale) {
            return; // This is the current scale.
        }
        skinScale = scale;
        // Changing GUI skin, reloading all screens:
        interfaceService.reload(new Runnable() {
            @Override
            public void run() {
                // Removing previous skin resources:
                VisUI.dispose();
                // Loading new skin:
                VisUI.load(scale);
                // Replacing the previously default skin:
                skinService.clear();
                skinService.addSkin("default", VisUI.getSkin());
            }
        });
    }
}