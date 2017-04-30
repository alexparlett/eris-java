package org.homonoia.sw.assets;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import lombok.extern.slf4j.Slf4j;
import org.homonoia.eris.resources.types.Json;
import org.homonoia.eris.resources.types.Locale;
import org.homonoia.eris.resources.types.json.JsonException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/04/2017
 */
@Slf4j
public class LocaleLoader implements AssetLoader {
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {

        try (InputStream inputStream = assetInfo.openStream()) {
            Json json = new Json();
            json.load(inputStream);

            Optional<Locale.Page[]> maybePages = json.fromJson(Locale.Page[].class);
            List<Locale.Page> pages = maybePages.map(Arrays::asList).orElse(Collections.emptyList());

            return new Locale(pages);
        } catch (JsonException e) {
            throw new IOException(e);
        }
    }
}
