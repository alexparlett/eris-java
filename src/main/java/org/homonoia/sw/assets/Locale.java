package org.homonoia.sw.assets;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
public class Locale {

    private final Map<Integer, Page> pages = new HashMap<>();

    public Locale(List<Page> pages) throws IOException {
        for (Page page : pages) {
            Page previous = this.pages.putIfAbsent(page.getId(), page);
            if (previous != null) {
                throw new IOException(MessageFormat.format("Locale contains duplicates Page {1}", page.getId()));
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

    public class Page {

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
