package org.homonoia.eris.core.components;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.events.frame.BeginFrame;
import org.homonoia.eris.events.frame.EndFrame;
import org.lwjgl.glfw.GLFW;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 12/12/2015
 */
@ContextualComponent
public class Clock extends Contextual {

    private double timeStep;
    private int frameNumber;

    @Autowired
    public Clock(final Context context) {
        super(context);
        timeStep = 0L;
        frameNumber = 0;
    }

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

    public double getTimeStep() {
        return timeStep;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public double getElapsedTime() {
        return GLFW.glfwGetTime();
    }
}
