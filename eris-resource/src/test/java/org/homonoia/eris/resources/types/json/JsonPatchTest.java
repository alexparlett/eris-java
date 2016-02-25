package org.homonoia.eris.resources.types.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.exceptions.JsonException;
import org.homonoia.eris.resources.types.JsonFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by alexparlett on 21/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonPatchTest {

    private Gson gson = new Gson();

    @Mock
    ApplicationContext applicationContext;

    @InjectMocks
    Context context;

    @Test(expected = NullPointerException.class)
    public void testPatch_FailureNoPath() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = NullPointerException.class)
    public void testPatch_FailureNoOp() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ path: '/a/e' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test
    public void testAdd_SuccessfulNamedProperty() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/a/e', value: 'true' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement e = root.getAsJsonObject().get("a").getAsJsonObject().get("e");
        assertThat(e, is(not(nullValue())));
        assertThat(e.getAsBoolean(), is(true));
    }

    @Test
    public void testAdd_SuccessfulIndex() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/c/0', value: false }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement e = root.getAsJsonObject().get("c").getAsJsonArray().get(0);
        assertThat(e, is(not(nullValue())));
        assertThat(e.getAsBoolean(), is(false));
    }

    @Test
    public void testAdd_SuccessfulEndArray() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/c/-', value: false }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement e = root.getAsJsonObject().get("c").getAsJsonArray().get(1);
        assertThat(e, is(not(nullValue())));
        assertThat(e.getAsBoolean(), is(false));
    }

    @Test
    public void testAdd_SuccessfulRootObject() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/', value: { g: false }}";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement g = root.getAsJsonObject().get("g");
        assertThat(g, is(not(nullValue())));
        assertThat(g.getAsBoolean(), is(false));
    }

    @Test
    public void testAdd_SuccessfulRootArray() throws IOException, JsonException {
        String json = "[ true, false ]";
        String patchJson = "{ op: 'add', path: '/', value: [ false, true ]}";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement g = root.getAsJsonArray().get(0);
        assertThat(g, is(not(nullValue())));
        assertThat(g.getAsBoolean(), is(false));
    }

    @Test(expected = JsonException.class)
    public void testAdd_FailureNamedPropertyNotFound() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/a/c/e', value: 'true' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testAdd_FailureIndexNotFound() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/c/1/e', value: 'true' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testAdd_FailureIndexOutOfBounds() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/c/1', value: 'true' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }


    @Test(expected = JsonException.class)
    public void testAdd_FailureNamedPropertyInvalid() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/a/b/e', value: 'true' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = NullPointerException.class)
    public void testAdd_FailureNoValue() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/a/e' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test
    public void testRemove_SuccessfulNamedProperty() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'remove', path: '/a/b' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement b = root.getAsJsonObject().get("a").getAsJsonObject().get("b");
        assertThat(b, is(nullValue()));
    }

    @Test
    public void testRemove_SuccessfulIndex() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'remove', path: '/c/0' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonArray array = root.getAsJsonObject().get("c").getAsJsonArray();
        assertThat(array.size(), is(0));
    }

    @Test(expected = JsonException.class)
    public void testRemove_FailureRoot() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'remove', path: '/' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testRemove_InvalidNamedProperty() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'remove', path: '/a/e' }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test
    public void testReplace_SuccessfulNamedProperty() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/a/b', value: 1 }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement b = root.getAsJsonObject().get("a").getAsJsonObject().get("b");
        assertThat(b, is(not(nullValue())));
        assertThat(b.getAsInt(), is(1));
    }

    @Test
    public void testReplace_SuccessfulIndex() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/c/0', value: false }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement c0 = root.getAsJsonObject().get("c").getAsJsonArray().get(0);
        assertThat(c0, is(not(nullValue())));
        assertThat(c0.getAsBoolean(), is(false));
    }

    @Test
    public void testReplace_SuccessfulRootObject() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/', value: { h: false }}";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        assertThat(root, is(patch.getValue()));
    }

    @Test
    public void testReplace_SuccessfulRootArray() throws JsonException, IOException {
        String json = "[ true, false ]";
        String patchJson = "{ op: 'replace', path: '/', value: [ false, true ]}";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        assertThat(root, is(patch.getValue()));
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureNamedPropertyDoesNotExist() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/a/c', value: 1 }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureIndexDoesNotExist() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/c/1', value: false }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureRootInvalidValue() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/', value: false }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureRootDifferentTypeObject() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/', value: [] }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureRootDifferentTypeArray() throws JsonException, IOException {
        String json = "[ true ]";
        String patchJson = "{ op: 'replace', path: '/', value: { foo: bar } }";

        when(applicationContext.getBean(Gson.class)).thenReturn(gson);

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }
}
