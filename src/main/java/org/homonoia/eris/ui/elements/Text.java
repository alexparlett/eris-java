package org.homonoia.eris.ui.elements;

import lombok.Getter;
import lombok.Setter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.types.Font;
import org.homonoia.eris.ui.UIElement;

import static java.util.Objects.nonNull;
import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 18/04/2017
 */
@Getter
@Setter
public class Text extends UIElement {

    private int x;
    private int y;
    private String text;
    private int align;
    private Font font;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public Text(Context context) {
        super(context);
    }

    @Override
    public void layout() {
        if (nonNull(font)) {
            nk_style_set_font(ui.getCtx(), font.getNkFont());
        }
        nk_layout_row_dynamic(ui.getCtx(), 20, 1);
        nk_label(ui.getCtx(), text, align);
    }
}
