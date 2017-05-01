package org.homonoia.sw.application;

import com.jme3.system.NanoTimer;
import de.lessvoid.nifty.spi.time.TimeProvider;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 30/04/2017
 */
public class NiftyNanoTimer extends NanoTimer implements TimeProvider {
    @Override
    public long getMsTime() {
        return (long) (getTimeInSeconds() * 1000L);
    }
}
