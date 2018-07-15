package org.homonoia.sw.scene.controllers.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import org.homonoia.sw.scene.ViewController;
import org.homonoia.sw.service.AssetService;
import org.homonoia.sw.service.InterfaceService;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
public class MapViewController extends ViewController {

    private TextArea actor;

    @Override
    public void create(InterfaceService interfaceService, AssetService assetService) {
        super.create(interfaceService, assetService);

        actor = new TextArea("FPS: ", interfaceService.getSkin());
        actor.setDisabled(true);
        stage.addActor(actor);
    }

    @Override
    public void update(float delta) {
        actor.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

        super.update(delta);
    }
}
