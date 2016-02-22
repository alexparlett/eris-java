package org.homonoia.eris.resources.cache;

import com.google.gson.Gson;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.JsonFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by alexparlett on 21/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceLoaderTest {

    @Mock
    ApplicationContext applicationContext;

    @Spy
    @InjectMocks
    Context context;

    @InjectMocks
    ResourceLoader resourceLoader;

    @Test
    public void testLoad_SuccessfulImmediate() throws URISyntaxException, IOException {

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());

        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("test.json").toURI());
        Resource resource = new JsonFile(context);

        resourceLoader.load(resource, path, true);

        assertThat(resource.getState(), is(Resource.AsyncState.SUCCESS));
    }


    @Test
    public void testLoad_FailureBadFileImmediate() throws URISyntaxException, IOException {

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());

        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("bad.json").toURI());
        Resource resource = new JsonFile(context);

        resourceLoader.load(resource, path, true);

        assertThat(resource.getState(), is(Resource.AsyncState.FAILED));
    }

    @Test(expected = NullPointerException.class)
    public void testLoad_FailureNoPath() throws URISyntaxException, IOException {

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());

        Resource resource = new JsonFile(context);

        resourceLoader.load(resource, null, true);
    }

    @Test(expected = NullPointerException.class)
    public void testLoad_FailureNoResource() throws URISyntaxException, IOException {

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());

        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("test.json").toURI());

        resourceLoader.load(null, path, true);
    }

    @Test(timeout = 5000L)
    public void testLoad_SuccessfulThreaded() throws URISyntaxException, IOException {

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());

        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("test.json").toURI());
        Resource resource = new JsonFile(context);

        resourceLoader.load(resource, path, false);

        while(resource.getState().equals(Resource.AsyncState.QUEUED) || resource.getState().equals(Resource.AsyncState.LOADING));

        assertThat(resource.getState(), is(Resource.AsyncState.SUCCESS));
    }

    @Test(timeout = 5000L)
    public void testLoad_FailureThreaded() throws URISyntaxException, IOException {

        when(applicationContext.getBean(Gson.class)).thenReturn(new Gson());

        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("bad.json").toURI());
        Resource resource = new JsonFile(context);

        resourceLoader.load(resource, path, false);

        while(resource.getState().equals(Resource.AsyncState.QUEUED) || resource.getState().equals(Resource.AsyncState.LOADING));

        assertThat(resource.getState(), is(Resource.AsyncState.FAILED));
    }
}