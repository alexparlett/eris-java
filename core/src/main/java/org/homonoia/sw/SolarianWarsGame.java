package org.homonoia.sw;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.github.czyzby.autumn.scanner.ClassScanner;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.homonoia.sw.scene.Scene;
import org.homonoia.sw.scene.controllers.map.MapViewController;
import org.homonoia.sw.scene.controllers.map.MapWorldController;
import org.homonoia.sw.service.AssetService;
import org.homonoia.sw.service.InterfaceService;
import org.homonoia.sw.service.PhysicsService;
import org.homonoia.sw.service.SceneService;

@Getter
public class SolarianWarsGame implements ApplicationListener {
    private final CommandLine args;
    private final ClassScanner scanner;
    private SceneService sceneService;
    private AssetService assetService;
    private PhysicsService physicsService;
    private InterfaceService interfaceService;

    public SolarianWarsGame(final CommandLine args, ClassScanner scanner) {
        this.args = args;
        this.scanner = scanner;
    }

    @Override
    public void create() {
        physicsService = new PhysicsService();
        assetService = new AssetService();
        interfaceService = new InterfaceService(assetService);
        sceneService = new SceneService(assetService, interfaceService);

        sceneService.add("map", new Scene(new MapViewController(), new MapWorldController()));
        sceneService.setCurrent("map");
    }

    @Override
    public void resize(int width, int height) {
        sceneService.resize(width, height);
    }

    @Override
    public void render() {
        sceneService.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
        sceneService.pause();
    }

    @Override
    public void resume() {
        sceneService.resume();
    }

    @Override
    public void dispose() {
        Disposables.disposeOf(physicsService, assetService, sceneService);
    }
}