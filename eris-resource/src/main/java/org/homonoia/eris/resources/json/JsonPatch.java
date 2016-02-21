package org.homonoia.eris.resources.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.homonoia.eris.resources.json.exceptions.JsonException;
import org.homonoia.eris.resources.json.exceptions.JsonPathException;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created by alexparlett on 14/02/2016.
 */
public class JsonPatch {

    private static final String REMOVE = "remove";
    private static final String ADD = "add";
    private static final String REPLACE = "replace";

    private String op;
    private String path;
    private JsonElement value;

    public String getOp() {
        return op;
    }

    public void setOp(final String op) {
        this.op = op;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(final JsonElement value) {
        this.value = value;
    }

    public void patch(final JsonElement patchableDocument) throws JsonException {
        Objects.requireNonNull(patchableDocument, "Invalid Patch. Patch target must not be null.");
        Objects.requireNonNull(path, "Invalid Patch. Patch path must not be null.");
        Objects.requireNonNull(op, "Invalid Patch. Patch op must not be null.");

        if (ADD.equals(getOp())) {
            add(patchableDocument);
        } else if (REMOVE.equals(getOp())) {
            remove(patchableDocument);
        } else if (REPLACE.equals(getOp())) {
            replace(patchableDocument);
        }
    }

    private void add(JsonElement patchableDocument) throws JsonException {
        Objects.requireNonNull(value, "Invalid Add. Patch value must not be null.");

        int lastIndexOf = getIndexOfTargetElementDelimiter();

        JsonElement origin = getOrigin(patchableDocument, lastIndexOf);

        if (origin.equals(patchableDocument)) {
            replaceRoot(patchableDocument);
        } else {
            String elementName = getElementName(lastIndexOf);

            if (JsonPath.NUMBER_PREDICATE.test(elementName) && origin.isJsonArray()) {
                JsonArray jsonArray = origin.getAsJsonArray();
                int targetIndex = Integer.parseInt(elementName);
                if (targetIndex < 0 || targetIndex >= jsonArray.size()) {
                    throw new JsonException("Invalid Add. Target index in array is out of bounds.");
                }
                jsonArray.add(value);
                for (int i = jsonArray.size() - 1; i > targetIndex && i < jsonArray.size(); i--) {
                    JsonElement temp = jsonArray.get(i - 1);
                    jsonArray.set(i - 1, jsonArray.get(i));
                    jsonArray.set(i, temp);
                }
            } else if (JsonPath.ARRAY_APPEND.equals(elementName) && origin.isJsonArray()) {
                origin.getAsJsonArray().add(value);
            } else if (origin.isJsonObject()) {
                origin.getAsJsonObject().add(elementName, value);
            } else {
                throw new JsonException("Invalid Add. Target element is neither an array or an object.");
            }
        }
    }

    private void remove(final JsonElement patchableDocument) throws JsonException {
        int lastIndexOf = getIndexOfTargetElementDelimiter();
        JsonElement origin = getOrigin(patchableDocument, lastIndexOf);

        if (origin.equals(patchableDocument)) {
            throw new JsonException("Invalid Remove. Cannot remove root.");
        }

        String elementName = getElementName(lastIndexOf);

        if (JsonPath.NUMBER_PREDICATE.test(elementName) && origin.isJsonArray()) {
            try {
                origin.getAsJsonArray().remove(Integer.parseInt(elementName));
            } catch (IndexOutOfBoundsException ex) {
                throw new JsonException("Invalid Replace. {} is not inside the array.", ex, elementName);
            }
        } else if (origin.isJsonObject()) {
            origin.getAsJsonObject().remove(elementName);
        } else {
            throw new JsonException("Invalid Remove. Target element is neither an array or an object.");
        }
    }

    private void replace(final JsonElement patchableDocument) throws JsonException {
        Objects.requireNonNull(value, "Invalid Replace. Patch value must not be null.");

        int lastIndexOf = getIndexOfTargetElementDelimiter();
        JsonElement origin = getOrigin(patchableDocument, lastIndexOf);

        if (origin.equals(patchableDocument)) {
            replaceRoot(patchableDocument);
        } else {
            String elementName = getElementName(lastIndexOf);

            if (JsonPath.NUMBER_PREDICATE.test(elementName) && origin.isJsonArray()) {
                try {
                    origin.getAsJsonArray().set(Integer.parseInt(elementName), value);
                } catch (IndexOutOfBoundsException ex) {
                    throw new JsonException("Invalid Replace. {} is not inside the array.", ex, elementName);
                }
            } else if (origin.isJsonObject()) {
                JsonObject jsonObject = origin.getAsJsonObject();
                if (!jsonObject.has(elementName)) {
                    throw new JsonException("Invalid Replace. {} does not exist on the target element", elementName);
                }
                jsonObject.add(elementName, value);
            } else {
                throw new JsonException("Invalid Replace. Target element is neither an array or an object.");
            }
        }
    }

    private int getIndexOfTargetElementDelimiter() throws JsonException {
        int lastIndexOf = path.lastIndexOf(JsonPath.PATH_DELIMITER);
        if (lastIndexOf < 0) {
            throw new JsonException("Invalid {}. Path[{}], must begin at the root.", op, path);
        }
        return lastIndexOf;
    }

    private JsonElement getOrigin(final JsonElement patchableDocument, final int lastIndexOf) throws JsonException {
        String queryPath = lastIndexOf > 0 ? path.substring(0, lastIndexOf) : path;
        JsonElement origin;
        try {
            origin = JsonPath.search(patchableDocument, queryPath)
                    .orElseThrow(() -> new JsonException("Invalid {}. No element found for Path[{}] in target json.", op, queryPath));
        } catch (JsonPathException ex) {
            throw new JsonException("Invalid {}. JsonPath could not search element.", ex, op);
        } catch (NullPointerException ex) {
            throw new JsonException("Invalid {}. JsonPath could not search element.", ex, op);
        }
        return origin;
    }

    private String getElementName(final int beginIndex) throws JsonException {
        String elementName;
        try {
            elementName = path.substring(beginIndex + 1);
        } catch (IndexOutOfBoundsException ex) {
            throw new JsonException("Invalid {}. Path[{}] does not contain a target element.", op, path);
        }

        if (StringUtils.isEmpty(elementName)) {
            throw new JsonException("Invalid {}. Path[{}] does not contain a target element.", op, path);
        }
        return elementName;
    }

    private void replaceRoot(final JsonElement patchableDocument) throws JsonException {
        if (patchableDocument.isJsonArray()) {
            if (!value.isJsonArray()) {
                throw new JsonException("Invalid {}. Attempting to replace root where the root is an array but the value specified in the patch is not an array.", op);
            }

            JsonArray jsonArray = patchableDocument.getAsJsonArray();
            int i = jsonArray.size() - 1;
            while(i >= 0 && i < jsonArray.size()) {
                jsonArray.remove(i);
                i = jsonArray.size() - 1;
            }
            jsonArray.addAll(value.getAsJsonArray());
        } else if (patchableDocument.isJsonObject()) {
            if (!value.isJsonObject()) {
                throw new JsonException("Invalid {}. Attempting to replace root where the root is an object but the value specified in the patch is not an object.", op);
            }

            JsonObject jsonObject = patchableDocument.getAsJsonObject();
            jsonObject.entrySet().clear();
            value.getAsJsonObject().entrySet().forEach(entry -> jsonObject.add(entry.getKey(), entry.getValue()));
        } else {
            throw new JsonException("Invalid {}. Attempting to replace root where the value is neither an array or an object.", op);
        }
    }
}
