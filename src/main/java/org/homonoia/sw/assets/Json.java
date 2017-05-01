package org.homonoia.sw.assets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonWriter;
import org.homonoia.sw.assets.json.JsonException;
import org.homonoia.sw.assets.json.JsonPatch;
import org.homonoia.sw.assets.json.JsonType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 14/02/2016
 */
public class Json {

    private static final Gson gson = new GsonBuilder()
            .setVersion(1.0)
            .serializeNulls()
            .setDateFormat(DateFormat.LONG)
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    private JsonElement root;

    public void createRoot(JsonType rootType) {
        switch (rootType) {
            case OBJECT:
                root = new JsonObject();
                break;
            case ARRAY:
                root = new JsonArray();
                break;
        }
    }

    public Optional<JsonElement> getRoot() {
        return Optional.ofNullable(root);
    }

    public void load(InputStream inputStream) throws IOException {

        List<JsonElement> elements = new ArrayList<>();

        try {
            JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(inputStream, "UTF-8"));
            parser.forEachRemaining(elements::add);

            if (elements.isEmpty()) {
                throw new IOException("Failed to load Json File. No Json Elements found.");
            } else if (elements.size() == 1) {
                root = elements.get(0);
            } else {
                root = new JsonArray();
                elements.stream().forEach(((JsonArray) root)::add);
            }
        } catch (JsonParseException | NoSuchElementException ex) {
            throw new IOException("Failed to load Json.", ex);
        }
    }

    public void save(OutputStream outputStream) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        gson.toJson(root, writer);
    }

    public void patch(JsonArray patchArray) throws JsonException {
        for (JsonElement element : patchArray) {
            patch(element);
        }
    }

    public void patch(JsonElement element) throws JsonException {
        try {
            JsonPatch patch = gson.fromJson(element, JsonPatch.class);
            patch.patch(root);
        } catch (JsonParseException ex) {
            throw new JsonException("Json Patching Failed with {}.", ex, element.toString());
        }
    }

    public <T> Optional<T> fromJson(Class<T> clazz) throws JsonException {
        try {
            return getRoot().map(root -> gson.fromJson(root, clazz));
        } catch (JsonParseException ex) {
            throw new JsonException("Failed to covert Json to {}", ex, clazz.getName());
        }
    }
}
