package org.homonoia.eris.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 14/04/2017
 */
@ToString
public class EmptyStatistics extends Statistics {

    private int frameCount = 0;
    private double elapsedTime = 0;
    private Frame current = new Frame();

    public void endFrame(double elapsedTime) {
        this.elapsedTime += elapsedTime;
        this.frameCount++;
    }

    public int getTotalFrames() {
        return frameCount;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void beginFrame() {
    }

    public Frame getCurrent() {
        return current;
    }

    @Setter
    @Getter
    @ToString
    public static class Frame extends Statistics.Frame {
        public void startSegment() {
        }

        public void endSegment(String name) {
        }
    }
}
