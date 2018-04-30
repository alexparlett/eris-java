package org.homonoia.sw.mvc.controller;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Inject;
import org.homonoia.sw.ecs.systems.RenderingSystem;
import org.homonoia.sw.mvc.component.ui.InterfaceService;
import org.homonoia.sw.mvc.stereotype.View;

/**
 * Thanks to View annotation, this class will be automatically found and initiated.
 * <p>
 * This is application's main view, displaying a menu with several options.
 */
@View(id = "game", value = "ui/templates/game.lml", themes = "music/theme.ogg")
public class GameController extends AshleyView {
    @Inject
    private InterfaceService interfaceService;

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        super.initialize(stage, actorMappedByIds);

        getEngine().addSystem(new RenderingSystem());
    }

    @Override
    public void show(Stage stage, Action action) {
        super.show(stage, action);

    }

    @Override
    public void render(final Stage stage, final float delta) {
        // As a proof of concept that you can pair custom logic with Autumn MVC views, this class implements
        // ViewRenderer and handles view rendering manually. It renders LibGDX logo before drawing the stage.
        getEngine().update(delta);
        stage.act(delta);
        stage.draw();
    }
}