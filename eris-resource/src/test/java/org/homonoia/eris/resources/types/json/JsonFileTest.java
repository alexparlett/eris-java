package org.homonoia.eris.resources.types.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.types.JsonFile;
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
 * Created by alexparlett on 21/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonFileTest {

    @Rule
    TemporaryFolder folder = new TemporaryFolder();

    @Mock
    ApplicationContext applicationContext;

    @InjectMocks
    Context context;

    @Test
    public void testLoad_Successful() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
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

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);
    }
}
