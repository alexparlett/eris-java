package org.homonoia.sw.mvc.component.asset.dto.provider;

import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import org.homonoia.sw.mvc.component.asset.AssetService;

/** Default provider for delayed asset injections.
 *
 * @author MJ */
public class AssetProvider implements ObjectProvider<Object> {
    private final AssetService assetService;
    private final String assetPath;
    private final Class<?> assetClass;
    private final boolean loadOnDemand;

    public AssetProvider(final AssetService assetService, final String assetPath, final Class<?> assetClass,
                         final boolean loadOnDemand) {
        this.assetService = assetService;
        this.assetPath = assetPath;
        this.assetClass = assetClass;
        this.loadOnDemand = loadOnDemand;
    }

    @Override
    public Object provide() {
        if (loadOnDemand) {
            return assetService.finishLoading(assetPath, assetClass);
        }
        if (!assetService.isLoaded(assetPath)) {
            // This will also schedule loading of the asset if it was previously unloaded.
            while (!assetService.isLoaded(assetPath)) {
                // Believe it or not, this is what LibGDX AssetManager does in finishLoading(String).
                assetService.update();
            }
        }
        return assetService.get(assetPath, assetClass);
    }
}