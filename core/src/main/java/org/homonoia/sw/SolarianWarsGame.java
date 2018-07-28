package org.homonoia.sw;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.github.czyzby.autumn.scanner.ClassScanner;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.homonoia.sw.assets.loaders.OgexLoader;
import org.homonoia.sw.scene.State;
import org.homonoia.sw.scene.controllers.map.MapViewController;
import org.homonoia.sw.scene.controllers.map.MapWorldController;
import org.homonoia.sw.service.AssetService;
import org.homonoia.sw.service.InterfaceService;
import org.homonoia.sw.service.PhysicsService;
import org.homonoia.sw.service.StateService;

@Getter
public class SolarianWarsGame implements ApplicationListener {
    public static final DefaultParser DEFAULT_PARSER = new DefaultParser();

    private final CommandLine args;
    private final ClassScanner scanner;
    private StateService stateService;
    private AssetService assetService;
    private PhysicsService physicsService;
    private InterfaceService interfaceService;

    public SolarianWarsGame(final String[] args, ClassScanner scanner) throws ParseException {
        this.scanner = scanner;
        this.args = DEFAULT_PARSER.parse(getOptions(), args);
    }

    @Override
    public void create() {
        createServices();
        createAssetLoaders();
        createState();
    }

    private void createState() {
        stateService.add("map", new State(new MapViewController(), new MapWorldController()));
        stateService.setCurrent("map");
    }

    private void createAssetLoaders() {
        assetService.getAssetManager().setLoader(Model.class, ".ogex", new OgexLoader(assetService.getAssetManager().getFileHandleResolver()));
        assetService.getEagerAssetManager().setLoader(Model.class, ".ogex", new OgexLoader(assetService.getEagerAssetManager().getFileHandleResolver()));
    }

    private void createServices() {
        physicsService = new PhysicsService();
        assetService = new AssetService();
        interfaceService = new InterfaceService(assetService);
        stateService = new StateService(assetService, interfaceService);
    }

    @Override
    public void resize(int width, int height) {
        stateService.resize(width, height);
    }

    @Override
    public void render() {
        stateService.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
        stateService.pause();
    }

    @Override
    public void resume() {
        stateService.resume();
    }

    @Override
    public void dispose() {
        Disposables.disposeOf(physicsService, assetService, stateService);
    }

    private Options getOptions() {
        Options options = new Options();
        options.addOption("d", "debug", false,"Turn on debug mode");
        options.addOption("q", "quickstart", false,"Start the game direct with default or last options");
        options.addOption("v", "version", false,"Shows the version");
        options.addOption("h", "help", false,"Shows the help message");

        return options;
    }
}