package org.homonoia.eris.engine;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Ini;
import org.homonoia.eris.resources.types.ini.IniException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by alexp on 25/02/2016.
 */
@ContextualComponent
public class Settings extends Contextual {

    private final ResourceCache resourceCache;

    private Ini ini;

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    @Autowired
    public Settings(final Context context, final ResourceCache resourceCache) {
        super(context);
        this.resourceCache = resourceCache;
    }

    void load() throws IOException, IniException {
    }

    void save() {
    }

    public Optional<String> getString(final String section, final String name) {
        return Optional.empty();
    }

    public Optional<Integer> getInteger(final String section, final String name) {
        return Optional.empty();
    }

    public Optional<Boolean> getBoolean(final String section, final String name) {
        return Optional.empty();
    }

    public Optional<Float> getFloat(final String section, final String name) {
        return Optional.empty();
    }
}
