package org.homonoia.eris.engine;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.scripting.ScriptBinding;
import org.homonoia.eris.scripting.ScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 01/03/2016
 */
public class Log extends Contextual implements ScriptBinding {

    @Autowired
    public Log(final Context context) {
        super(context);
    }

    @Override
    public void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindGlobal("log", this);
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
            context.putProperty("LEVEL", getContext().isDebugEnabled() ? "DEBUG" : "INFO");
            configurator.doConfigure(ClassLoader.getSystemResource("logback.xml"));
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }

        context.start();
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public void shutdown() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.stop();
    }

    public void info(String src, String msg, Object... args) {
        Logger logger = getLogger(src);
        logger.info(msg, args);
    }

    public void warn(String src, String msg, Object... args) {
        Logger logger = getLogger(src);
        logger.warn(msg, args);
    }

    public void error(String src, String msg, Object... args) {
        Logger logger = getLogger(src);
        logger.error(msg, args);
    }

    public void debug(String src, String msg, Object... args) {
        Logger logger = getLogger(src);
        logger.debug(msg, args);
    }

    public void trace(String src, String msg, Object... args) {
        Logger logger = getLogger(src);
        logger.trace(msg, args);
    }

    private Logger getLogger(String src) {
        if (src.endsWith("$py.class")) {
            Path path = Paths.get(src.replace("$py.class", ".py"));
            src = path.getFileName().toString();
        }
        return LoggerFactory.getLogger(src);
    }
}
