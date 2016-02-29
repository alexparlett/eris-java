package org.homonoia.eris.engine;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.homonoia.eris.resources.types.json.JsonException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by alexp on 25/02/2016.
 */
@ContextualComponent
public class Locale extends Contextual {

    public static final String EMPTY_VALUE = "";
    public static final String LOCALES_PATH = "Locales";
    public static final String LOCALES_EXTENSION = ".lang";

    private final ResourceCache resourceCache;

    private final Map<Integer, Page> pages = new HashMap<>();

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    @Autowired
    public Locale(final Context context, ResourceCache resourceCache) {
        super(context);
        this.resourceCache = resourceCache;
    }

    public void load(final String language) throws IOException, JsonException {
        Objects.requireNonNull(language, "Language must not be null.");

        pages.clear();

        Path fullPath = Paths.get(LOCALES_PATH, language, LOCALES_EXTENSION);
        Json json = resourceCache.get(Json.class, fullPath);
        if (json != null) {
            Optional<Page[]> maybePages = json.fromJson(Page[].class);
            List<Page> pages = maybePages.map(Arrays::asList).orElse(Collections.emptyList());

            for(Page page : pages) {
                Page previous = this.pages.putIfAbsent(page.getId(), page);
                if (previous != null) {
                    throw new IOException(MessageFormat.format("Locale {0} contains duplicates Page {1}", json.getPath(), page.getId()));
                }
            }
        }
    }

    public String localize(final int page, final int line) {
        Page find = pages.get(page);
        return find != null ? find.getLine(line) : EMPTY_VALUE;
    }

    public void replace(String line, String... values) {
        MessageFormat.format(line, values);
    }

    private class Page {

        private Integer id;
        private Map<Integer, String> lines = new HashMap<>();

        private String getLine(final int line) {
            return lines.getOrDefault(line, EMPTY_VALUE);
        }

        private Integer getId() {
            return id;
        }

        private void setId(final Integer id) {
            this.id = id;
        }

        private Map<Integer, String> getLines() {
            return lines;
        }

        private void setLines(final Map<Integer, String> lines) {
            this.lines = lines;
        }
    }
}
