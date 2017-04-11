package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.graphics.drawables.atlas.SubTexture;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.Image;
import org.homonoia.eris.resources.types.Json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 03/01/2017
 */
public class TextureAtlas extends Texture2D {

    private Map<String, SubTexture> subTextures = new TreeMap<>();

    public TextureAtlas(Context context) {
        super(context);
    }

    @Override
    public void load(InputStream inputStream) throws IOException {
        ResourceCache resourceCache = getContext().getBean(ResourceCache.class);

        Json json = new Json(getContext());
        json.load(inputStream);

        JsonObject root = json.getRoot()
                .map(JsonElement::getAsJsonObject)
                .orElseThrow(() -> new IOException("Failed to load Texture2D. Metadata Json invalid."));

        root.get("textures")
                .getAsJsonArray()
                .forEach(jsonElement -> {
                    JsonObject texture = jsonElement.getAsJsonObject();
                    int x = texture.get("x").getAsInt();
                    int y = texture.get("y").getAsInt();
                    int width = texture.get("w").getAsInt();
                    int height = texture.get("h").getAsInt();
                    String name = texture.get("name").getAsString();

                    subTextures.put(name, SubTexture.builder()
                            .x(x)
                            .y(y)
                            .width(width)
                            .height(height)
                            .build());
                });

        String file = root.get("file").getAsString();
        Image image = resourceCache.getTemporary(Image.class, file)
                .orElseThrow(() -> new IOException("Failed to load Texture2D. Metadata Json doesn't contain valid file: " + file));

        parseParameters(root);
        compile(image);
    }

    public Map<String, SubTexture> getSubTextures() {
        return subTextures;
    }
}
