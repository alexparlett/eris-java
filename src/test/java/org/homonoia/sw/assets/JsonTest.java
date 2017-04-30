package org.homonoia.eris.resources.types;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.homonoia.sw.assets.Json;
import org.homonoia.sw.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.EmptyStatistics;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 21/02/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
    public void testLoad_Successful() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        MatcherAssert.assertThat(root.isJsonObject(), Is.is(true));

        JsonElement a = root.getAsJsonObject().get("a");
        MatcherAssert.assertThat(a.getAsJsonObject(), Is.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(a.getAsJsonObject().get("b").getAsInt(), Is.is(0));

        JsonElement c = root.getAsJsonObject().get("c");
        MatcherAssert.assertThat(c.getAsJsonArray(), Is.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(c.getAsJsonArray().get(0).getAsBoolean(), Is.is(true));

        JsonElement d = root.getAsJsonObject().get("d");
        MatcherAssert.assertThat(d.getAsJsonNull(), Is.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test(expected = IOException.class)
    public void testLoad_Failure_BadJson() throws Exception {
        String json = "{ a: { b: 0 , c: [ true ], d: null }";

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);
    }
}
