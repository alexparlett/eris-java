package org.homonoia.sw.controller;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import org.homonoia.sw.mvc.component.ui.controller.ViewController;
import org.homonoia.sw.mvc.component.ui.controller.ViewInitializer;
import org.homonoia.sw.mvc.component.ui.controller.ViewRenderer;
import org.homonoia.sw.mvc.component.ui.controller.ViewResizer;
import org.homonoia.sw.mvc.component.ui.controller.ViewShower;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/04/2018
 */
public abstract class AshleyView implements ViewInitializer, ViewRenderer, ViewShower, InputProcessor, ViewResizer {

    @Inject
    private InputMultiplexer inputMultiplexer;

    @Getter(AccessLevel.PROTECTED)
    private Engine engine = new Engine();

    @Getter(AccessLevel.PROTECTED)
    private Stage stage;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        this.stage = stage;
    }

    @Override
    public void destroy(ViewController viewController) {
        engine.removeAllEntities();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void resize(Stage stage, int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void show(Stage stage, Action action) {
        stage.addAction(Actions.sequence(action, Actions.run(() -> inputMultiplexer.addProcessor(this))));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void hide(Stage stage, Action action) {
        stage.addAction(Actions.sequence(action, Actions.run(() -> inputMultiplexer.removeProcessor(this))));
        stage.addAction(action);
    }

    @Override
    public void render(Stage stage, float delta) {
        engine.update(delta);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public boolean keyDown(int keycode) {
        return stage.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return stage.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return stage.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return stage.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return stage.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return stage.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return stage.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return stage.scrolled(amount);
    }
}