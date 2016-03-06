package org.homonoia.eris.engine;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.io.FileSystem;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Ini;
import org.homonoia.eris.resources.types.ini.IniException;
import org.homonoia.eris.resources.types.ini.IniSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
@ContextualComponent
public class Settings extends Contextual {

    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

    private final ResourceCache resourceCache;
    private final FileSystem fileSystem;
    private Ini ini;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    @Autowired
    public Settings(final Context context, final ResourceCache resourceCache, final FileSystem fileSystem) {
        super(context);
        this.resourceCache = resourceCache;
        this.fileSystem = fileSystem;
    }

    void load() throws IOException {
        ini = resourceCache.get(Ini.class, Paths.get("settings.ini")).orElseThrow(() -> new IOException("settings.ini not found in Application Directories."));
    }

    void save() throws IOException {
        OutputStream outputStream = fileSystem.newOutputStream(FileSystem.getApplicationDataDirectory().resolve("settings.ini"));
        ini.save(outputStream);
    }

    public Optional<String> getString(final String section, final String name) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(name);

        return ini.get(section).map(iniSection -> iniSection.get(name)).orElse(Optional.empty());
    }

    public Optional<Integer> getInteger(final String section, final String name) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(name);

        return ini.get(section).map(iniSection -> iniSection.getInteger(name)).orElse(Optional.empty());
    }

    public Optional<Boolean> getBoolean(final String section, final String name) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(name);

        return ini.get(section).map(iniSection -> iniSection.getBoolean(name)).orElse(Optional.empty());
    }

    public Optional<Float> getFloat(final String section, final String name) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(name);

        return ini.get(section).map(iniSection -> iniSection.getFloat(name)).orElse(Optional.empty());
    }

    public void setString(final String section, final String name, final String value) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        IniSection iniSection = ini.get(section).orElseGet(() -> {
            try {
                return ini.add(section);
            } catch (IniException e) {
                LOG.error("Failed to set {} {} to {}.", section, name, value, e);
                return null;
            }
        });

        Objects.requireNonNull(iniSection);

        iniSection.set(name, value);
    }

    public void setInteger(final String section, final String name, final Integer value) {
        setString(section, name, value.toString());
    }

    public void setBoolean(final String section, final String name, final Boolean value) {
        setString(section, name, value.toString());
    }

    public void setFloat(final String section, final String name, final Float value) {
        setString(section, name, value.toString());
    }
}
