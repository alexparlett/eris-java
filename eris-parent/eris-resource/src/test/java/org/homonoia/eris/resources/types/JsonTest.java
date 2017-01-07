package org.homonoia.eris.resources.types;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.homonoia.eris.core.Context;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

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
    private ApplicationContext applicationContext;

    @InjectMocks
    private Context context;

    @Test
    public void testLoad_Successful() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        assertThat(root.isJsonObject(), is(true));

        JsonElement a = root.getAsJsonObject().get("a");
        assertThat(a.getAsJsonObject(), is(not(nullValue())));
        assertThat(a.getAsJsonObject().get("b").getAsInt(), is(0));

        JsonElement c = root.getAsJsonObject().get("c");
        assertThat(c.getAsJsonArray(), is(not(nullValue())));
        assertThat(c.getAsJsonArray().get(0).getAsBoolean(), is(true));

        JsonElement d = root.getAsJsonObject().get("d");
        assertThat(d.getAsJsonNull(), is(not(nullValue())));
    }

    @Test(expected = IOException.class)
    public void testLoad_Failure_BadJson() throws Exception {
        String json = "{ a: { b: 0 , c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Json jsonFile = new Json(context);
        jsonFile.load(inputStream);
    }
}
