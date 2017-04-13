package org.homonoia.eris.resources.types;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonWriter;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.json.JsonException;
import org.homonoia.eris.resources.types.json.JsonPatch;
import org.homonoia.eris.resources.types.json.JsonType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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
public class Json extends Resource {

    private final Gson gson;
    private JsonElement root;

    public Json(final Context context) {
        super(context);
        this.gson = context.getBean(Gson.class);
    }

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

    @Override
    public void load(final InputStream inputStream) throws IOException {

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

    @Override
    public void save() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(getLocation().toFile())) {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
            gson.toJson(root, writer);
        } catch (JsonParseException ex) {
            throw new IOException("Failed to save Json.", ex);
        }
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
            throw new JsonException("Json Patching Failed on {} with {}.", ex, getPath().toString(), element.toString());
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
