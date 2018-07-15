package org.homonoia.sw.scene;

import com.badlogic.gdx.utils.Disposable;
import org.homonoia.sw.service.AssetService;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
public abstract class WorldController implements Disposable {

    protected World world;

    public void create(AssetService assetService) {
        world = new World();
    }

    public void show() {
    }

    public void update(float delta) {
        world.act(delta);
    }

    public void hide() {

    }

    public void destory() {

    }

    public boolean keyDown(int keycode) {
        return world.keyDown(keycode);
    }

    public boolean keyUp(int keycode) {
        return world.keyUp(keycode);
    }

    public boolean keyTyped(char character) {
        return world.keyTyped(character);
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return world.touchDown(screenX, screenY, pointer, button);
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return world.touchUp(screenX, screenY, pointer, button);
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return world.touchDragged(screenX, screenY, pointer);
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return world.mouseMoved(screenX, screenY);
    }

    public boolean scrolled(int amount) {
        return world.scrolled(amount);
    }

    public void resize(int width, int height) {
        world.resize(width, height);
    }

    public void resume() {

    }

    public void pause() {

    }

    @Override
    public void dispose() {
        world.dispose();
        world = null;
    }
}
