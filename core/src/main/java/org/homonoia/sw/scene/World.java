package org.homonoia.sw.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;
import lombok.Setter;
import org.homonoia.sw.ecs.core.Engine;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
@Getter
@Setter
public class World implements Disposable {

    private Environment environment = new Environment();
    private Engine engine = new Engine();
    private CameraInputController cameraInputController;
    private Viewport viewport;

    public World() {
        this.viewport = new ScreenViewport(new PerspectiveCamera(66f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public World(Viewport viewport) {
        this.viewport = viewport;
    }

    public World(Viewport viewport, CameraInputController cameraInputController) {
        this.viewport = viewport;
        this.cameraInputController = cameraInputController;
    }

    public void act(float delta) {
        engine.update(delta);
    }

    public boolean keyDown(int keycode) {
        if (nonNull(cameraInputController)) {
            return cameraInputController.keyDown(keycode);
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        if (nonNull(cameraInputController)) {
            return cameraInputController.keyUp(keycode);
        }
        return false;
    }

    public boolean keyTyped(char character) {
        if (nonNull(cameraInputController)) {
            return cameraInputController.keyTyped(character);
        }
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (nonNull(cameraInputController)) {
            return cameraInputController.touchDown(screenX, screenY, pointer, button);
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (nonNull(cameraInputController)) {
            return cameraInputController.touchUp(screenX, screenY, pointer, button);
        }
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (nonNull(cameraInputController)) {
            return cameraInputController.touchDragged(screenX, screenY, pointer);
        }
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        if (nonNull(cameraInputController)) {
            return cameraInputController.mouseMoved(screenX, screenY);
        }
        return false;
    }

    public boolean scrolled(int amount) {
        if (nonNull(cameraInputController)) {
            return cameraInputController.scrolled(amount);
        }
        return false;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void dispose() {
        viewport = null;

        cameraInputController = null;

        environment.clear();
        environment = null;

        engine.dispose();
        engine = null;
    }
}
