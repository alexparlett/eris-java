package org.homonoia.eris.ui.elements;

import lombok.Getter;
import lombok.Setter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.ui.UIElement;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkStyleWindow;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 18/04/2017
 */
@Getter
@Setter
public class Panel extends UIElement {

    private int x;
    private int y;
    private Integer width;
    private Integer height;
    private String title;
    private int flags;
    private List<UIElement> children = new ArrayList<>();
    private NkStyleWindow nkStyleWindow = NkStyleWindow.create();

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public Panel(Context context) {
        super(context);
    }

    @Override
    public void layout() {
        Objects.requireNonNull(title);
        Objects.requireNonNull(width);
        Objects.requireNonNull(height);

        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            ui.getCtx().style().window(nkStyleWindow);
            if (nk_begin(
                    ui.getCtx(),
                    title,
                    nk_rect(x, y, width, height, rect),
                    flags
            )) {
                children.stream()
                        .filter(UIElement::isActive)
                        .forEach(UIElement::layout);
            }
            nk_end(ui.getCtx());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        children.forEach(Contextual::destroy);
    }
}
