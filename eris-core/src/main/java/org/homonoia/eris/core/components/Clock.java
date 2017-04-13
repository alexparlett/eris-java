package org.homonoia.eris.core.components;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.frame.BeginFrame;
import org.homonoia.eris.events.frame.EndFrame;
import org.lwjgl.glfw.GLFW;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 12/12/2015
 */
public class Clock extends Contextual {

    private double timeStep;
    private int frameNumber;

    public Clock(final Context context) {
        super(context);
        context.registerBean(this);
        timeStep = 0L;
        frameNumber = 0;
    }

    /**
     * Starts a new frame.
     *
     * @param timeStep The time step in milliseconds.
     */
    public void beginFrame(final double timeStep) {
        this.frameNumber++;
        this.timeStep = timeStep;

        BeginFrame.Builder eventBuilder = BeginFrame.builder()
                .frameNumber(this.frameNumber)
                .timeStep(this.timeStep);

        publish(eventBuilder);
    }

    public void endFrame() {
        publish(EndFrame.builder());
    }

    /**
     * @return The time step in milliseconds.
     */
    public double getTimeStep() {
        return timeStep;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    /**
     * @return The elapsed time in milliseconds.
     */
    public double getElapsedTime() {
        return GLFW.glfwGetTime() * 1000.0;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
