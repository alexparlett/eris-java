package org.homonoia.eris.renderer;

import lombok.Data;
import org.homonoia.eris.graphics.drawables.Model;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/03/2017
 */
@Data
public class DebugMode {

    private boolean boundingBoxes = true;
    private boolean axis = false;
    private boolean frustum = false;

    private Model boundingBoxCube;
}
