package org.homonoia.eris.graphics.drawables.atlas;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 03/01/2017
 */
@Getter
@Builder
@EqualsAndHashCode
public class SubTexture {
    private int x;
    private int y;
    private int width;
    private int height;
}
