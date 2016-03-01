package org.homonoia.eris.engine;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.io.FileSystem;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alexparlett on 01/03/2016.
 */
@ContextualComponent
public class Log extends Contextual {

    @Autowired
    public Log(final Context context) {
        super(context);
    }

    public void initialize() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            context.putProperty("APP_DIR", FileSystem.getApplicationDataDirectory().toString());
            configurator.doConfigure(ClassLoader.getSystemResource("logback.xml"));
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }

        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public void shutdown() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.stop();
    }
}
