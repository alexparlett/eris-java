package org.homonoia.sw.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import org.homonoia.sw.service.AssetService;
import org.homonoia.sw.service.InterfaceService;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 25/05/2018
 */
public class State implements Disposable, InputProcessor {

    private ViewController viewController;
    private WorldController worldController;

    public State(ViewController viewController, WorldController worldController) {
        this.viewController = viewController;
        this.worldController = worldController;
    }

    public void create(AssetService assetService, InterfaceService interfaceService) {
        this.viewController.create(interfaceService, assetService);
        this.worldController.create(assetService);
    }

    public void show() {
        Gdx.input.setInputProcessor(this);

        viewController.show();
        worldController.show();
    }

    public void update(float delta) {
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        worldController.update(delta);
        viewController.update(delta);
    }

    public void pause() {
        worldController.pause();
    }

    public void resume() {
        worldController.resume();
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);

        worldController.hide();
        viewController.hide();
    }

    public void resize(int width, int height) {
        worldController.resize(width, height);
        viewController.resize(width, height);
    }

    public boolean keyDown(int keycode) {
        return viewController.keyDown(keycode) || worldController.keyDown(keycode);
    }

    public boolean keyUp(int keycode) {
        return viewController.keyUp(keycode) || worldController.keyUp(keycode);
    }

    public boolean keyTyped(char character) {
        return viewController.keyTyped(character) || worldController.keyTyped(character);
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return viewController.touchDown(screenX, screenY, pointer, button) || worldController.touchDown(screenX, screenY, pointer, button);
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return viewController.touchUp(screenX, screenY, pointer, button) || worldController.touchUp(screenX, screenY, pointer, button);
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return viewController.touchDragged(screenX, screenY, pointer) || worldController.touchDragged(screenX, screenY, pointer);
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return viewController.mouseMoved(screenX, screenY) || worldController.mouseMoved(screenX, screenY);
    }

    public boolean scrolled(int amount) {
        return viewController.scrolled(amount) || worldController.scrolled(amount);
    }

    @Override
    public void dispose() {
        Disposables.disposeOf(worldController, viewController);
    }
}
