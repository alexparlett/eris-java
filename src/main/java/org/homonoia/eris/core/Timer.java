package org.homonoia.eris.core;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 05/02/2016
 */
public class Timer {

    private double startTime;

    public Timer() {
        startTime = System.currentTimeMillis();
    }

    public double getElapsedTime() {
        return getElapsedTime(false);
    }

    public double getElapsedTime(boolean reset) {
        double currentTime = System.currentTimeMillis();
        double elapsedTime =  currentTime - startTime;

        if (reset) {
            startTime = currentTime;
        }

        return elapsedTime;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }

}
