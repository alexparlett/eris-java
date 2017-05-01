package org.homonoia.sw.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.homonoia.sw.application.Game;
import org.homonoia.sw.application.NiftyNanoTimer;

import javax.annotation.Nonnull;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/04/2017
 */
public class LoadingAppState extends AbstractAppState implements ScreenController {

    private AppStateManager stateManager;
    private Game app;
    private Nifty nifty;
    private Screen screen;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.app = (Game) app;
        this.nifty = this.app.getNiftyJmeDisplay().getNifty();

        this.screen = new Screen(nifty, "LoadingAppScreen", this, (NiftyNanoTimer) app.getTimer());
        this.nifty.addScreen("LoadingAppScreen", screen);
        this.nifty.gotoScreen("LoadingAppScreen");

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }
}
