package org.homonoia.sw.service;

import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Disposable;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 26/05/2018
 */
public class PhysicsService implements Disposable {

    public PhysicsService() {
        Bullet.init(true, true);
    }

    @Override
    public void dispose() {
    }
}
