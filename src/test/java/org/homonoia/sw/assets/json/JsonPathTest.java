package org.homonoia.sw.assets.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.homonoia.sw.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.EmptyStatistics;
import org.homonoia.sw.assets.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 21/02/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonPathTest {

    @Mock
    CommandLineArgs commandLineArgs;

    @InjectMocks
    Context context;

    @Before
    public void setup() {
        context.registerBean(new EmptyStatistics());
        context.registerBean(new Gson());
    }

    @Test
    public void testSearch_SuccessfulNamedProperty() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonElement result = JsonPath.search(root, "/a/b").orElseThrow(() -> new Exception("Element not found."));

        MatcherAssert.assertThat(result.getAsInt(), Is.is(0));
    }

    @Test
    public void testSearch_SuccessfulArrayIndex() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonElement result = JsonPath.search(root, "/c/0").orElseThrow(() -> new Exception("Element not found."));

        MatcherAssert.assertThat(result.getAsBoolean(), Is.is(true));
    }

    @Test
    public void testSearch_SuccessfulRoot() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonElement result = JsonPath.search(root, "/").orElseThrow(() -> new Exception("Element not found."));

        MatcherAssert.assertThat(result, Is.is(root));
    }

    @Test(expected = NoSuchElementException.class)
    public void testSearch_FailureNoElement() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonPath.search(root, "/a/e").orElseThrow(() -> new NoSuchElementException("Element not found."));
    }

    @Test(expected = JsonPathException.class)
    public void testSearch_FailureNoRoot() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonPath.search(root, "a/b").orElseThrow(() -> new Exception("Element not found."));
    }


    @Test(expected = JsonPathException.class)
    public void testSearch_FailureNoPath() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonPath.search(root, null).orElseThrow(() -> new Exception("Element not found."));
    }
}
