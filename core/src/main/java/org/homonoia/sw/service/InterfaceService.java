package org.homonoia.sw.service;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import lombok.Getter;
import org.homonoia.sw.scene.ViewController;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
@Getter
public class InterfaceService implements Disposable {

    private Batch batch = new SpriteBatch();
    private ObjectMap<String, ViewController> views = GdxMaps.newObjectMap();

    @Override
    public void dispose() {
        batch.dispose();
        batch = null;

        Disposables.disposeOf(views);
        views.clear();
    }
}
