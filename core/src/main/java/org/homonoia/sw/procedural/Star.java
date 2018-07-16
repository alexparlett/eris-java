package org.homonoia.sw.procedural;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Star {
    private Color color;
    private Vector3 position;
}
