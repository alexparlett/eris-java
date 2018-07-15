package org.homonoia.sw.service;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import org.homonoia.sw.scene.Scene;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
public class SceneService implements Disposable {

    private final AssetService assetService;
    private final InterfaceService interfaceService;
    private ObjectMap<String, Scene> scenes = GdxMaps.newObjectMap();
    private Scene current;

    public SceneService(final AssetService assetService, final InterfaceService interfaceService) {
        this.assetService = assetService;
        this.interfaceService = interfaceService;
    }

    public void add(String id, Scene scene) {
        if (!scenes.containsKey(id)) {
            scenes.put(id, scene);
            scene.create(assetService, interfaceService);
        }
    }

    public void remove(String id) {
        Scene scene = scenes.remove(id);
        scene.dispose();
    }

    public void setCurrent(String id) {
        if (nonNull(current)) {
            current.hide();
        }
        current = scenes.get(id);
        current.show();
    }

    public void resize(int width, int height) {
        current.resize(width, height);
    }

    public void update(float deltaTime) {
        current.update(deltaTime);
    }

    public void pause() {
        current.pause();
    }

    public void resume() {
        current.resume();
    }

    @Override
    public void dispose() {
        Disposables.disposeOf(scenes);
        scenes.clear();
    }
}
