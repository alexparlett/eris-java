package org.homonoia.sw.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.homonoia.sw.service.InterfaceService;

import static com.badlogic.gdx.Gdx.gl;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
public abstract class ViewController implements Disposable {

    protected Stage stage;

    public void create(final InterfaceService interfaceService) {
        Viewport viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport);
    }

    public void show() {
    }

    public void update(float delta) {
        stage.act(delta);
        gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        stage.draw();
    }

    public void hide() {

    }

    public void destory() {

    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public boolean keyDown(int keycode) {
        return stage.keyDown(keycode);
    }

    public boolean keyUp(int keycode) {
        return stage.keyUp(keycode);
    }

    public boolean keyTyped(char character) {
        return stage.keyTyped(character);
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return stage.touchDown(screenX, screenY, pointer, button);
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return stage.touchUp(screenX, screenY, pointer, button);
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return stage.touchDragged(screenX, screenY, pointer);
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return stage.mouseMoved(screenX, screenY);
    }

    public boolean scrolled(int amount) {
        return stage.scrolled(amount);
    }

    @Override
    public void dispose() {
        stage.dispose();
        stage = null;
    }
}
