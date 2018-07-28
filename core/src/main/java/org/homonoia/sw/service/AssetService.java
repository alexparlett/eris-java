
package org.homonoia.sw.service;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;

/**
 * Wraps around two internal {@link AssetManager}s, providing utilities for asset loading.
 * Allows to load assets both eagerly and by constant updating, without forcing loading of "lazy" assets upon "eager"
 * request, like AssetManager does (see {@link AssetManager#finishLoadingAsset(String)}
 * implementation - it basically loads everything, waiting for a specific asset to get loaded). Note that some wrapped
 * methods provide additional utility and validations, so direct access to the managers is not advised.
 *
 * @author MJ
 */
public class AssetService implements Disposable {
    private final AssetManager assetManager = new AssetManager();
    /**
     * There is no reliable way of keeping both eagerly and normally loaded assets together, while preserving a way to
     * both load assets by constant updating and load SOME assets at once. That's why this service uses two managers.
     */
    private final AssetManager eagerAssetManager = new AssetManager();

    private final ObjectSet<String> scheduledAssets = GdxSets.newSet();
    private final Array<Runnable> onLoadActions = GdxArrays.newArray();

    /**
     * @param loader     asset loader for the selected type. Will be registered in all managed {@link AssetManager}
     *                   instances.
     * @param assetClass class of the loaded asset.
     * @param <Type>     type of registered loader.
     * @see AssetManager#setLoader(Class, AssetLoader)
     */
    public <Type> void registerLoader(final AssetLoader<Type, AssetLoaderParameters<Type>> loader,
                                      final Class<Type> assetClass) {
        assetManager.setLoader(assetClass, loader);
        eagerAssetManager.setLoader(assetClass, loader);
    }

    /**
     * @param loader     asset loader for the selected type. Will be registered in all managed {@link AssetManager}
     *                   instances.
     * @param suffix     allows to filter files.
     * @param type class of the loaded asset.
     * @see AssetManager#setLoader(Class, String, AssetLoader)
     */
    public  <T, P extends AssetLoaderParameters<T>> void registerLoader(Class<T> type, String suffix,
                                                                    AssetLoader<T, P> loader) {
        assetManager.setLoader(type, suffix, loader);
        eagerAssetManager.setLoader(type, suffix, loader);
    }

    /**
     * Schedules loading of the selected asset, if it was not scheduled already.
     *
     * @param assetPath  internal path to the asset.
     * @param assetClass class of the asset.
     */
    public void load(final String assetPath, final Class<?> assetClass) {
        load(assetPath, assetClass, null);
    }

    /**
     * Schedules loading of the selected asset, if it was not scheduled already.
     *
     * @param assetPath         assetPath internal path to the asset.
     * @param assetClass        assetClass class of the asset.
     * @param loadingParameters specific loading parameters.
     * @param <Type>            type of asset class to load.
     */
    public <Type> void load(final String assetPath, final Class<Type> assetClass,
                            final AssetLoaderParameters<Type> loadingParameters) {
        if (isAssetNotScheduled(assetPath)) {
            assetManager.load(assetPath, assetClass, loadingParameters);
        }
    }

    private boolean isAssetNotScheduled(final String assetPath) {
        return !isLoaded(assetPath) && !scheduledAssets.contains(assetPath);
    }

    /**
     * @param assetPath internal path to the asset.
     * @return true if the asset is fully loaded.
     */
    public boolean isLoaded(final String assetPath) {
        return assetManager.isLoaded(assetPath) || eagerAssetManager.isLoaded(assetPath);
    }

    /**
     * Schedules disposing of the selected asset.
     *
     * @param assetPath internal path to the asset.
     */
    public void unload(final String assetPath) {
        if (assetManager.isLoaded(assetPath) || scheduledAssets.contains(assetPath)) {
            assetManager.unload(assetPath);
        } else if (eagerAssetManager.isLoaded(assetPath)) {
            eagerAssetManager.unload(assetPath);
        }
    }

    /**
     * Immediately loads all scheduled assets.
     */
    public void finishLoading() {
        assetManager.finishLoading();
        doOnLoadingFinish();
    }

    private void invokeOnLoadActions() {
        for (final Runnable action : onLoadActions) {
            if (action != null) {
                action.run();
            }
        }
        onLoadActions.clear();
    }

    /**
     * Immediately loads the chosen asset. Schedules loading of the asset if it wasn't selected to be loaded already.
     *
     * @param assetPath  internal path to the asset.
     * @param assetClass class of the loaded asset.
     * @param <Type>     type of asset class to load.
     * @return instance of the loaded asset.
     */
    public <Type> Type finishLoading(final String assetPath, final Class<Type> assetClass) {
        return finishLoading(assetPath, assetClass, null);
    }

    /**
     * Immediately loads the chosen asset. Schedules loading of the asset if it wasn't selected to be loaded already.
     *
     * @param assetPath         internal path to the asset.
     * @param assetClass        class of the loaded asset.
     * @param loadingParameters used if asset is not already loaded.
     * @param <Type>            type of asset class to load.
     * @return instance of the loaded asset.
     */
    public <Type> Type finishLoading(final String assetPath, final Class<Type> assetClass,
                                     final AssetLoaderParameters<Type> loadingParameters) {
        if (assetManager.isLoaded(assetPath)) {
            return assetManager.get(assetPath, assetClass);
        }
        if (!eagerAssetManager.isLoaded(assetPath)) {
            eagerAssetManager.load(assetPath, assetClass, loadingParameters);
            eagerAssetManager.finishLoadingAsset(assetPath);
        }
        return eagerAssetManager.get(assetPath, assetClass);
    }

    /**
     * Manually updates wrapped asset manager.
     *
     * @return true if all scheduled assets are loaded.
     */
    public boolean update() {
        final boolean isLoaded = assetManager.update();
        if (isLoaded) {
            doOnLoadingFinish();
        }
        return isLoaded;
    }

    private void doOnLoadingFinish() {
        invokeOnLoadActions();
    }

    /**
     * @return progress of asset loading. Does not include eagerly loaded assets.
     */
    public float getLoadingProgress() {
        return assetManager.getProgress();
    }

    /**
     * @return progress of asset loading. Includes eagerly loaded assets.
     */
    public float getTotalLoadingProgress() {
        return assetManager.getProgress() * eagerAssetManager.getProgress();
    }

    /**
     * @return direct reference to internal {@link AssetManager} instance. Use with care.
     * @see #getEagerAssetManager()
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * @return direct reference to internal {@link AssetManager} used for eager asset loading. For synchronous asset
     * loading purposes. Use with care.
     */
    public AssetManager getEagerAssetManager() {
        return eagerAssetManager;
    }

    /**
     * @param assetPath  internal path to the asset.
     * @param assetClass class of the asset.
     * @param <Type>     type of asset class to get.
     * @return an instance of the loaded asset, if available.
     */
    public <Type> Type get(final String assetPath, final Class<Type> assetClass) {
        if (assetManager.isLoaded(assetPath)) {
            return assetManager.get(assetPath, assetClass);
        }
        return eagerAssetManager.get(assetPath, assetClass);
    }

    @Override
    public void dispose() {
        Disposables.disposeOf(assetManager, eagerAssetManager);
    }

    /**
     * @param action will be executed after all currently scheduled assets are loaded. This requires an
     *               {@link #update()} or {@link #finishLoading()} call.
     */
    public void addOnLoadAction(final Runnable action) {
        onLoadActions.add(action);
    }
}
