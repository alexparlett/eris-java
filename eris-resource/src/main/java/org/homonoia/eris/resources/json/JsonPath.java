package org.homonoia.eris.resources.json;

import com.google.gson.JsonElement;
import org.homonoia.eris.resources.json.exceptions.JsonPathException;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by alexparlett on 14/02/2016.
 */
public final class JsonPath {

    public static final String ARRAY_APPEND = "-";
    public static final String PATH_DELIMITER = "/";
    public static final Predicate<String> NUMBER_PREDICATE = Pattern.compile("\\d+").asPredicate();

    public static Optional<JsonElement> search(final JsonElement root, final String path) throws JsonPathException {
        JsonElement current = Objects.requireNonNull(root, "JsonElement to be searched cannot be null.");
        if (StringUtils.isEmpty(path)) {
            throw new JsonPathException("Path to search for cannot be empty.");
        } else if (!path.startsWith(PATH_DELIMITER)) {
            throw new JsonPathException("Path must begin at root.");
        }

        if (PATH_DELIMITER.compareTo(path) == 0) {
            return Optional.of(current);
        }

        String[] split = path.substring(1).split(PATH_DELIMITER);
        for (int i = 0; i < split.length && current != null; i++) {
            String segment = split[i];
            if (NUMBER_PREDICATE.test(segment) && current.isJsonArray()) {
                try {
                    current = current.getAsJsonArray().get(Integer.parseInt(segment));
                } catch (IndexOutOfBoundsException ex) {
                    current = null;
                }
            } else {
                current = current.getAsJsonObject().get(segment);
            }
        }

        return Optional.ofNullable(current);
    }
}
