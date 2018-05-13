package org.homonoia.sw.mvc.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import lombok.AccessLevel;
import lombok.Getter;
import org.homonoia.sw.ecs.core.Engine;
import org.homonoia.sw.mvc.component.ui.controller.ViewController;
import org.homonoia.sw.mvc.component.ui.controller.ViewInitializer;
import org.homonoia.sw.mvc.component.ui.controller.ViewRenderer;
import org.homonoia.sw.mvc.component.ui.controller.ViewResizer;
import org.homonoia.sw.mvc.component.ui.controller.ViewShower;
import org.homonoia.sw.mvc.component.ui.controller.impl.StandardViewResizer;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/04/2018
 */
public abstract class Scene implements ViewInitializer, ViewRenderer, ViewShower, InputProcessor, ViewResizer {

    @Inject
    @Getter(AccessLevel.PROTECTED)
    private InputMultiplexer inputMultiplexer;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    private Engine engine = new Engine();

    @Getter(AccessLevel.PROTECTED)
    private Stage stage;

    @Getter(AccessLevel.PROTECTED)
    private Environment environment = new Environment();

    private StandardViewResizer viewResizer = new StandardViewResizer();

    @Override
    @OverridingMethodsMustInvokeSuper
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        this.stage = stage;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void destroy(ViewController viewController) {
        Disposables.disposeOf(engine, stage);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void resize(Stage stage, int width, int height) {
        viewResizer.resize(stage, width, height);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void show(Stage stage, Action action) {
        stage.addAction(Actions.sequence(action, Actions.run(() -> inputMultiplexer.addProcessor(this))));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void hide(Stage stage, Action action) {
        stage.addAction(Actions.sequence(Actions.run(() -> inputMultiplexer.removeProcessor(this)), action));
    }

    @Override
    public void render(Stage stage, float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        engine.update(delta);
        stage.act(delta);

        gl.glClear(GL_DEPTH_BUFFER_BIT);

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
