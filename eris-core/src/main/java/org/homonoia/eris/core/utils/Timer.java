package org.homonoia.eris.core.utils;

import org.lwjgl.glfw.GLFW;

/**
 * Created by alexparlett on 05/02/2016.
 */
public class Timer {

    private double startTime;

    public Timer() {
        startTime = GLFW.glfwGetTime();
    }

    public double getElapsedTime() {
        return getElapsedTime(false);
    }

    public double getElapsedTime(boolean reset) {
        double currentTime = GLFW.glfwGetTime();
        double elapsedTime = startTime - currentTime;

        if (reset) {
            startTime = currentTime;
        }

        return elapsedTime;
    }

    public void reset() {
        startTime = GLFW.glfwGetTime();
    }

}
