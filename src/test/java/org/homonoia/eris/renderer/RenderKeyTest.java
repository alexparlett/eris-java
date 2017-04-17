package org.homonoia.eris.renderer;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 12/11/2016
 */
public class RenderKeyTest {

    @Test
    public void testCommandOrdering() {
        RenderKey rk1 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(1)
                .build();

        RenderKey rk2 = RenderKey.builder()
                .command(2)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(1)
                .build();

        List<RenderKey> renderKeys = Arrays.asList(rk1, rk2);
        renderKeys.sort(RenderKey::compareTo);

        assertThat(renderKeys.get(0), is(rk1));
        assertThat(renderKeys.get(1), is(rk2));
    }

    @Test
    public void testTargetOrdering() {
        RenderKey rk1 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(1)
                .build();

        RenderKey rk2 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(2)
                .targetLayer(1)
                .transparency(0)
                .depth(1)
                .build();

        List<RenderKey> renderKeys = Arrays.asList(rk1, rk2);
        renderKeys.sort(RenderKey::compareTo);

        assertThat(renderKeys.get(0), is(rk1));
        assertThat(renderKeys.get(1), is(rk2));
    }

    @Test
    public void testTargetLayerOrdering() {
        RenderKey rk1 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(1)
                .build();

        RenderKey rk2 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(2)
                .transparency(0)
                .depth(1)
                .build();

        List<RenderKey> renderKeys = Arrays.asList(rk1, rk2);
        renderKeys.sort(RenderKey::compareTo);

        assertThat(renderKeys.get(0), is(rk1));
        assertThat(renderKeys.get(1), is(rk2));
    }

    @Test
    public void testDepthOrdering_NonTransparent() {
        RenderKey rk1 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(1)
                .build();

        RenderKey rk2 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(2)
                .build();

        List<RenderKey> renderKeys = Arrays.asList(rk1, rk2);
        renderKeys.sort(RenderKey::compareTo);

        assertThat(renderKeys.get(0), is(rk1));
        assertThat(renderKeys.get(1), is(rk2));
    }

    @Test
    public void testDepthOrdering_NonTransparent_DifferentMaterial() {
        RenderKey rk1 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(1)
                .build();

        RenderKey rk2 = RenderKey.builder()
                .command(1)
                .material(2)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(2)
                .build();

        RenderKey rk3 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(0)
                .depth(3)
                .build();

        List<RenderKey> renderKeys = Arrays.asList(rk1, rk2, rk3);
        renderKeys.sort(RenderKey::compareTo);

        assertThat(renderKeys.get(0), is(rk1));
        assertThat(renderKeys.get(1), is(rk3));
        assertThat(renderKeys.get(2), is(rk2));
    }

    @Test
    public void testDepthOrdering_Transparent() {
        RenderKey rk1 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(1)
                .depth(1)
                .build();

        RenderKey rk2 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(1)
                .depth(2)
                .build();

        List<RenderKey> renderKeys = Arrays.asList(rk1, rk2);
        renderKeys.sort(RenderKey::compareTo);

        assertThat(renderKeys.get(0), is(rk2));
        assertThat(renderKeys.get(1), is(rk1));
    }

    @Test
    public void testDepthOrdering_Transparent_DifferentMaterial() {
        RenderKey rk1 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(1)
                .depth(1)
                .build();

        RenderKey rk2 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(1)
                .targetLayer(1)
                .transparency(1)
                .depth(2)
                .build();

        RenderKey rk3 = RenderKey.builder()
                .command(1)
                .material(2)
                .target(1)
                .targetLayer(1)
                .transparency(1)
                .depth(3)
                .build();

        List<RenderKey> renderKeys = Arrays.asList(rk1, rk2, rk3);
        renderKeys.sort(RenderKey::compareTo);

        assertThat(renderKeys.get(0), is(rk3));
        assertThat(renderKeys.get(1), is(rk2));
        assertThat(renderKeys.get(2), is(rk1));
    }


    @Test
    public void testDifferentTransparent() {
        RenderKey rk0 = RenderKey.builder()
                .command(0)
                .material(0)
                .target(0)
                .targetLayer(0)
                .transparency(0)
                .depth(0)
                .build();

        RenderKey rk1 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(0)
                .targetLayer(0)
                .transparency(0)
                .depth(1)
                .build();

        RenderKey rk2 = RenderKey.builder()
                .command(1)
                .material(1)
                .target(0)
                .targetLayer(0)
                .transparency(1)
                .depth(2)
                .build();

        RenderKey rk3 = RenderKey.builder()
                .command(1)
                .material(2)
                .target(0)
                .targetLayer(0)
                .transparency(0)
                .depth(3)
                .build();

        List<RenderKey> renderKeys = Arrays.asList(rk0, rk2, rk1, rk3);
        renderKeys.sort(RenderKey::compareTo);

        assertThat(renderKeys.get(0), is(rk0));
        assertThat(renderKeys.get(1), is(rk1));
        assertThat(renderKeys.get(2), is(rk3));
        assertThat(renderKeys.get(3), is(rk2));
    }
}