package org.homonoia.sw.mvc.component.ui.processor;

import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import org.homonoia.sw.mvc.component.ui.InterfaceService;
import org.homonoia.sw.mvc.component.ui.controller.ViewDialogController;
import org.homonoia.sw.mvc.component.ui.controller.impl.AnnotatedViewDialogController;
import org.homonoia.sw.mvc.stereotype.ViewDialog;

/** Processes {@link ViewDialog} components. Initiates dialog controllers.
 *
 * @author MJ */
public class ViewDialogAnnotationProcessor extends AbstractAnnotationProcessor<ViewDialog> {
    @Inject private InterfaceService interfaceService;

    @Override
    public Class<ViewDialog> getSupportedAnnotationType() {
        return ViewDialog.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final ViewDialog annotation, final Object component,
                            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (component instanceof ViewDialogController) {
            interfaceService.registerDialogController(type, (ViewDialogController) component);
        } else {
            interfaceService.registerDialogController(type,
                    new AnnotatedViewDialogController(annotation, component, interfaceService));
        }
    }
}