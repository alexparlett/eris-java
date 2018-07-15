package org.homonoia.sw.service;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;
import lombok.Getter;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2018
 */

public class InterfaceService implements Disposable {

    private final AssetService assetService;

    @Getter
    private Batch batch;

    public InterfaceService(final AssetService assetService) {
        this.assetService = assetService;
        this.batch = new SpriteBatch();

        VisUI.load("ui/skin.json");
    }

    public Skin getSkin() {
        return VisUI.getSkin();
    }

    @Override
    public void dispose() {
        VisUI.dispose(true);
    }
}
