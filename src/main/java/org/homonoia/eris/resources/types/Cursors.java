package org.homonoia.eris.resources.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.cache.ResourceCache;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.*;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 22/04/2017
 */
public class Cursors extends Resource {

    private Image image;

    public Cursors(Context context) {
        super(context);
    }

    @Override
    public void load(InputStream inputStream) throws IOException {
        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new JsonIOException("No root element found"));

        String imageLocation = root.getAsJsonPrimitive("image").getAsString();

        this.image = resourceCache.get(Image.class, imageLocation)
                .orElseThrow(() -> new IOException(MessageFormat.format("{0} not found for Cursors {1}", imageLocation, getLocation())));

        Spliterator<JsonElement> cursors = Spliterators.spliteratorUnknownSize(root.getAsJsonArray("cursors").iterator(), Spliterator.ORDERED);
        StreamSupport.stream(cursors, false).map(JsonElement::getAsJsonObject)
                .forEach(cursor -> {

                });

        setState(AsyncState.SUCCESS);
    }
}
