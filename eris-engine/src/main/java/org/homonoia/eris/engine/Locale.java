package org.homonoia.eris.engine;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Json;
import org.homonoia.eris.resources.types.json.JsonException;
import org.homonoia.eris.scripting.ScriptBinding;
import org.homonoia.eris.scripting.ScriptEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
public class Locale extends Contextual implements ScriptBinding {

    private static final String LOCALES_PATH = "Locales";
    private static final String LOCALES_EXTENSION = ".lang";

    private final ResourceCache resourceCache;

    private final Map<Integer, Page> pages = new HashMap<>();

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public Locale(final Context context, ResourceCache resourceCache) {
        super(context);
        this.resourceCache = resourceCache;
    }

    @Override
    public void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindGlobal("locale", this);
    }

    public void load(final String language) throws IOException, JsonException {
        Objects.requireNonNull(language, "Language must not be null.");

        pages.clear();

        Path fullPath = Paths.get(LOCALES_PATH, language + LOCALES_EXTENSION);
        Json json = resourceCache.get(Json.class, fullPath).orElseThrow(() -> new IOException(MessageFormat.format("Failed to load Locale {0} at {1}. File does not exist.", language, fullPath)));
        if (json != null) {
            Optional<Page[]> maybePages = json.fromJson(Page[].class);
            List<Page> pages = maybePages.map(Arrays::asList).orElse(Collections.emptyList());

            for (Page page : pages) {
                Page previous = this.pages.putIfAbsent(page.getId(), page);
                if (previous != null) {
                    throw new IOException(MessageFormat.format("Locale {0} contains duplicates Page {1}", json.getPath(), page.getId()));
                }
            }
        }
    }

    public String localize(final int page, final int line) {
        Page find = pages.get(page);
        return find != null ? find.getLine(line) : null;
    }

    public static String replace(String line, Object... values) {
        return MessageFormat.format(line, values);
    }

    private class Page {

        private Integer id;

        @JsonAdapter(LineAdapter.class)
        private Map<Integer, String> lines = new HashMap<>();

        private String getLine(final int line) {
            return lines.getOrDefault(line, null);
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

        private class LineAdapter extends TypeAdapter<Map<Integer, String>> {
            @Override
            public void write(final JsonWriter out, final Map<Integer, String> value) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public Map<Integer, String> read(final JsonReader in) throws IOException {
                Map<Integer, String> jsonMap = new HashMap<>();
                in.beginArray();
                while (in.peek().equals(JsonToken.BEGIN_OBJECT)) {
                    in.beginObject();

                    // Get Id
                    in.nextName();
                    int id = in.nextInt();

                    // Get Value
                    in.nextName();
                    String value = in.nextString();

                    jsonMap.put(id, value);

                    in.endObject();
                }
                in.endArray();

                return jsonMap;
            }
        }
    }
}
