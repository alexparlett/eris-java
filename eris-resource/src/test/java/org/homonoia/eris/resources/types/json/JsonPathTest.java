package org.homonoia.eris.resources.types.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.exceptions.JsonPathException;
import org.homonoia.eris.resources.types.JsonFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by alexparlett on 21/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonPathTest {

    @Mock
    ApplicationContext applicationContext;

    @InjectMocks
    Context context;

    @Test
    public void testSearch_SuccessfulNamedProperty() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonElement result = JsonPath.search(root, "/a/b").orElseThrow(() -> new Exception("Element not found."));

        assertThat(result.getAsInt(), is(0));
    }

    @Test
    public void testSearch_SuccessfulArrayIndex() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonElement result = JsonPath.search(root, "/c/0").orElseThrow(() -> new Exception("Element not found."));

        assertThat(result.getAsBoolean(), is(true));
    }

    @Test
    public void testSearch_SuccessfulRoot() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonElement result = JsonPath.search(root, "/").orElseThrow(() -> new Exception("Element not found."));

        assertThat(result, is(root));
    }

    @Test(expected = NoSuchElementException.class)
    public void testSearch_FailureNoElement() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonPath.search(root, "/a/e").orElseThrow(() -> new NoSuchElementException("Element not found."));
    }

    @Test(expected = JsonPathException.class)
    public void testSearch_FailureNoRoot() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonPath.search(root, "a/b").orElseThrow(() -> new Exception("Element not found."));
    }


    @Test(expected = JsonPathException.class)
    public void testSearch_FailureNoPath() throws Exception {
        String json = "{ a: { b: 0 }, c: [ true ], d: null }";

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());


        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        JsonFile jsonFile = new JsonFile(context);
        jsonFile.load(inputStream);

        JsonElement root = jsonFile.getRoot().orElseThrow(() -> new Exception("Root not found."));

        JsonPath.search(root, null).orElseThrow(() -> new Exception("Element not found."));
    }
}
