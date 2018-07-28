package org.homonoia.sw.service;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import org.homonoia.sw.scene.State;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
public class StateService implements Disposable {

    private final AssetService assetService;
    private final InterfaceService interfaceService;
    private ObjectMap<String, State> scenes = GdxMaps.newObjectMap();
    private State current;

    public StateService(final AssetService assetService, final InterfaceService interfaceService) {
        this.assetService = assetService;
        this.interfaceService = interfaceService;
    }

    public void add(String id, State state) {
        if (!scenes.containsKey(id)) {
            scenes.put(id, state);
            state.create(assetService, interfaceService);
        }
    }

    public void remove(String id) {
        State state = scenes.remove(id);
        state.dispose();
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
