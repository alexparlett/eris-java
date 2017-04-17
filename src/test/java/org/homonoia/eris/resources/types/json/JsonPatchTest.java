package org.homonoia.eris.resources.types.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.EmptyStatistics;
import org.homonoia.eris.resources.types.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 21/02/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonPatchTest {

    private final Gson gson = new Gson();

    @Mock
    CommandLineArgs commandLineArgs;

    @InjectMocks
    Context context;

    @Before
    public void setup() {
        context.registerBean(new EmptyStatistics());
        context.registerBean(new Gson());
    }

    @Test(expected = NullPointerException.class)
    public void testPatch_FailureNoPath() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = NullPointerException.class)
    public void testPatch_FailureNoOp() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ path: '/a/e' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test
    public void testAdd_SuccessfulNamedProperty() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/a/e', value: 'true' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement e = root.getAsJsonObject().get("a").getAsJsonObject().get("e");
        MatcherAssert.assertThat(e, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(e.getAsBoolean(), CoreMatchers.is(true));
    }

    @Test
    public void testAdd_SuccessfulIndex() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/c/0', value: false }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement e = root.getAsJsonObject().get("c").getAsJsonArray().get(0);
        MatcherAssert.assertThat(e, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(e.getAsBoolean(), CoreMatchers.is(false));
    }

    @Test
    public void testAdd_SuccessfulEndArray() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/c/-', value: false }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement e = root.getAsJsonObject().get("c").getAsJsonArray().get(1);
        MatcherAssert.assertThat(e, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(e.getAsBoolean(), CoreMatchers.is(false));
    }

    @Test
    public void testAdd_SuccessfulRootObject() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/', value: { g: false }}";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement g = root.getAsJsonObject().get("g");
        MatcherAssert.assertThat(g, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(g.getAsBoolean(), CoreMatchers.is(false));
    }

    @Test
    public void testAdd_SuccessfulRootArray() throws IOException, JsonException {
        String json = "[ true, false ]";
        String patchJson = "{ op: 'add', path: '/', value: [ false, true ]}";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement g = root.getAsJsonArray().get(0);
        MatcherAssert.assertThat(g, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(g.getAsBoolean(), CoreMatchers.is(false));
    }

    @Test(expected = JsonException.class)
    public void testAdd_FailureNamedPropertyNotFound() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/a/c/e', value: 'true' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testAdd_FailureIndexNotFound() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/c/1/e', value: 'true' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testAdd_FailureIndexOutOfBounds() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/c/1', value: 'true' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }


    @Test(expected = JsonException.class)
    public void testAdd_FailureNamedPropertyInvalid() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/a/b/e', value: 'true' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = NullPointerException.class)
    public void testAdd_FailureNoValue() throws IOException, JsonException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'add', path: '/a/e' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test
    public void testRemove_SuccessfulNamedProperty() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'remove', path: '/a/b' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement b = root.getAsJsonObject().get("a").getAsJsonObject().get("b");
        MatcherAssert.assertThat(b, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testRemove_SuccessfulIndex() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'remove', path: '/c/0' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonArray array = root.getAsJsonObject().get("c").getAsJsonArray();
        MatcherAssert.assertThat(array.size(), CoreMatchers.is(0));
    }

    @Test(expected = JsonException.class)
    public void testRemove_FailureRoot() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'remove', path: '/' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testRemove_InvalidNamedProperty() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'remove', path: '/a/e' }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test
    public void testReplace_SuccessfulNamedProperty() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/a/b', value: 1 }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement b = root.getAsJsonObject().get("a").getAsJsonObject().get("b");
        MatcherAssert.assertThat(b, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(b.getAsInt(), CoreMatchers.is(1));
    }

    @Test
    public void testReplace_SuccessfulIndex() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/c/0', value: false }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        JsonElement c0 = root.getAsJsonObject().get("c").getAsJsonArray().get(0);
        MatcherAssert.assertThat(c0, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(c0.getAsBoolean(), CoreMatchers.is(false));
    }

    @Test
    public void testReplace_SuccessfulRootObject() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/', value: { h: false }}";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        MatcherAssert.assertThat(root, CoreMatchers.is(patch.getValue()));
    }

    @Test
    public void testReplace_SuccessfulRootArray() throws JsonException, IOException {
        String json = "[ true, false ]";
        String patchJson = "{ op: 'replace', path: '/', value: [ false, true ]}";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);

        MatcherAssert.assertThat(root, CoreMatchers.is(patch.getValue()));
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureNamedPropertyDoesNotExist() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/a/c', value: 1 }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureIndexDoesNotExist() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/c/1', value: false }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureRootInvalidValue() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/', value: false }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureRootDifferentTypeObject() throws JsonException, IOException {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";
        String patchJson = "{ op: 'replace', path: '/', value: [] }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }

    @Test(expected = JsonException.class)
    public void testReplace_FailureRootDifferentTypeArray() throws JsonException, IOException {
        String json = "[ true ]";
        String patchJson = "{ op: 'replace', path: '/', value: { foo: bar } }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonPatch patch = gson.fromJson(patchJson, JsonPatch.class);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new NoSuchElementException("Root not found"));
        patch.patch(root);
    }
}
