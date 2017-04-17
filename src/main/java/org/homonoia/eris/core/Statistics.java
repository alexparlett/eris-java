package org.homonoia.eris.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

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
    private Frame current = new Frame();

    public int getTotalFrames() {
        return frames.size();
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public Frame getCurrent() {
        return current;
    }

    public void beginFrame() {
        if (nonNull(current)) {
            frames.add(current);
        }
        current = new Frame();
    }

    public void endFrame(double elapsedTime) {
        this.elapsedTime += elapsedTime;
        current.setElapsedTime(elapsedTime);
        current.end();
    }

    @Setter
    @Getter
    @ToString
    public static class Frame {
        private double startTime;
        private double elapsedTime;
        private Map<String, Double> segments = Collections.synchronizedMap(new LinkedHashMap<>());
        private double segmentStart;

        public Frame() {
            startTime = System.currentTimeMillis();
        }

        public void startSegment() {
            segmentStart = System.currentTimeMillis();
        }

        public void endSegment(String name) {
            segments.put(name, System.currentTimeMillis() - segmentStart);
        }

        public void end() {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
    }
}
