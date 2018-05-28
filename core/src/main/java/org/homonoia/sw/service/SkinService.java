package org.homonoia.sw.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.processor.event.MessageDispatcher;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.collection.disposable.DisposableArray;
import com.github.czyzby.kiwi.util.gdx.file.CommonFileExtension;
import org.homonoia.sw.mvc.config.AutumnActionPriority;
import org.homonoia.sw.mvc.config.AutumnMessage;
import org.homonoia.sw.mvc.dto.SkinData;
import org.homonoia.sw.mvc.processor.SkinAnnotationProcessor;
import org.homonoia.sw.mvc.processor.SkinAssetAnnotationProcessor;

/** Manages application's {@link Skin}.
 *
 * @author MJ */
public class SkinService {
    private final DisposableArray<Skin> skins = DisposableArray.newArray();
    private final ObjectMap<String, Skin> skinMap = GdxMaps.newObjectMap();
    private Skin defaultSkin;

    @Initiate(priority = AutumnActionPriority.TOP_PRIORITY)
    private void initiateSkin(final SkinAssetAnnotationProcessor skinAssetProcessor,
                              final SkinAnnotationProcessor skinProcessor,
                              final AssetService assetService,
                              final MessageDispatcher messageDispatcher) {
        final ObjectMap<String, SkinData> skinsData = skinProcessor.getSkinsData();
        for (final Entry<String, SkinData> skinData : skinsData) {
            final Skin skin = initiateSkin(skinAssetProcessor, skinData.value, assetService, messageDispatcher);
            skins.add(skin);
            skinMap.put(skinData.key, skin);
            if (skinData.value.isDefault()) {
                defaultSkin = skin;
            }
        }
        messageDispatcher.postMessage(AutumnMessage.SKINS_LOADED);
    }

    private static Skin initiateSkin(final SkinAssetAnnotationProcessor skinAssetProcessor, final SkinData skinData,
                                     final AssetService assetService, final MessageDispatcher messageDispatcher) {
        final Skin skin = new Skin();
        final String atlasPath = skinData.getPath() + CommonFileExtension.ATLAS;
        assetService.load(atlasPath, TextureAtlas.class);
        final TextureAtlas skinAtlas = assetService.finishLoading(atlasPath, TextureAtlas.class);

        final String[] fontPaths = skinData.getFonts();
        loadFonts(atlasPath, fontPaths, assetService);
        skin.addRegions(skinAtlas);
        assignFonts(skin, skinData, fontPaths, assetService);

        skin.load(Gdx.files.internal(skinData.getPath() + CommonFileExtension.JSON));
        return skin;
    }

    private static void loadFonts(final String atlasPath, final String[] fontPaths, final AssetService assetService) {
        if (fontPaths.length != 0) {
            final BitmapFontParameter loadingParameters = new BitmapFontParameter();
            loadingParameters.atlasName = atlasPath;
            for (final String fontPath : fontPaths) {
                assetService.finishLoading(fontPath, BitmapFont.class, loadingParameters);
            }
        }
    }

    private static void assignFonts(final Skin skin, final SkinData skinData, final String[] fontPaths,
                                    final AssetService assetService) {
        if (fontPaths.length != 0) {
            final String[] fontNames = skinData.getFontsNames();
            for (int fontIndex = 0; fontIndex < fontPaths.length; fontIndex++) {
                skin.add(fontNames[fontIndex], assetService.get(fontPaths[fontIndex], BitmapFont.class),
                        BitmapFont.class);
            }
        }
    }

    /** @return application's main {@link Skin} used to build views. */
    public Skin getSkin() {
        return defaultSkin;
    }

    /** @param id ID of the requested skin. By default, case is ignored.
     * @return {@link Skin} with the selected ID. */
    public Skin getSkin(final String id) {
        return skinMap.get(id.toLowerCase());
    }

    /** @param id ID of the skin. By default, case is ignored.
     * @param skin will be registered in LML parser and disposed by this service when the application is closed. */
    public void addSkin(final String id, final Skin skin) {
        skins.add(skin);
        skinMap.put(id.toLowerCase(), skin);
    }

    /** @return internally stored array of all current skins. */
    public DisposableArray<Skin> getSkins() {
        return skins;
    }

    /** Removes all internally stored skins. Does not affect LML parser. */
    public void clear() {
        skins.clear();
        skinMap.clear();
    }

    @Destroy(priority = AutumnActionPriority.VERY_LOW_PRIORITY)
    private void dispose() {
        Disposables.gracefullyDisposeOf((Disposable) skins);
    }
}