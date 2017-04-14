package org.homonoia.eris.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 14/04/2017
 */
@ToString
public class Statistics {

    private List<Frame> frames = new ArrayList<>();
    private double elapsedTime = 0;
    private Frame current;

    public void endFrame(double elapsedTime) {
        this.elapsedTime += elapsedTime;
        current.setElapsedTime(elapsedTime);
        frames.add(current);
    }

    public int getTotalFrames() {
        return frames.size();
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void beginFrame() {
        current = new Frame();
    }

    public Frame getCurrent() {
        return current;
    }

    @Setter
    @Getter
    @ToString
    public static class Frame {
        private double elapsedTime;
        private Map<String, Double> segments = new LinkedHashMap<>();
        private double segmentStart;

        public void startSegment() {
            segmentStart = System.currentTimeMillis();
        }

        public void endSegment(String name) {
            segments.put(name, System.currentTimeMillis() - segmentStart);
        }
    }
}
