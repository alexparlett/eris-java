package org.homonoia.sw;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import org.apache.commons.cli.CommandLine;
import org.homonoia.sw.scene.Scene;
import org.homonoia.sw.scene.controllers.map.MapViewController;
import org.homonoia.sw.scene.controllers.map.MapWorldController;
import org.homonoia.sw.service.PhysicsService;
import org.homonoia.sw.scene.SceneManager;
import org.homonoia.sw.service.InterfaceService;

public class SolarianWarsGame implements ApplicationListener {
    public final CommandLine args;
    public PhysicsService physicsService;
    public InterfaceService interfaceService;
    public SceneManager sceneManager;

    public SolarianWarsGame(final CommandLine args) {
        this.args = args;
    }

    @Override
    public void create() {
        this.physicsService = new PhysicsService();
        this.interfaceService = new InterfaceService();
        this.sceneManager = new SceneManager();

        sceneManager.add("map", new Scene(new MapViewController(), new MapWorldController()));
        sceneManager.setCurrent("map");
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.resize(width, height);
    }

    @Override
    public void render() {
        sceneManager.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
        sceneManager.pause();
    }

    @Override
    public void resume() {
        sceneManager.resume();
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        physicsService.dispose();
        interfaceService.dispose();
    }
}