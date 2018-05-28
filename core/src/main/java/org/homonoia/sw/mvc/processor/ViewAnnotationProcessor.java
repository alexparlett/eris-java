package org.homonoia.sw.mvc.processor;

import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import org.homonoia.sw.mvc.controller.ViewController;
import org.homonoia.sw.service.AssetService;
import org.homonoia.sw.service.InterfaceService;
import org.homonoia.sw.stereotype.View;

/** Processes {@link View} components. Initiates controllers.
 *
 * @author MJ */
public class ViewAnnotationProcessor extends AbstractAnnotationProcessor<View> {
    @Inject private InterfaceService interfaceService;
    @Inject private AssetService assetService;

    @Override
    public Class<View> getSupportedAnnotationType() {
        return View.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final View annotation, final Object component, final Context context,
                            final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        interfaceService.registerController(type, (ViewController) component);
    }
}