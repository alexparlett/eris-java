package org.homonoia.eris.renderer;

import lombok.Data;
import org.homonoia.eris.graphics.drawables.Model;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/04/2017
 */
@Data
public class DebugMode {

    private boolean grid = false;
    private boolean boundingBoxes = false;
    private boolean axis = false;

    private Model boundingBoxCube;
}
