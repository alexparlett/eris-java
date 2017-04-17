package org.homonoia.eris.resources.cache;

import com.google.gson.Gson;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.EmptyStatistics;
import org.homonoia.eris.core.FileSystem;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 21/02/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceLoaderTest {

    @Mock
    CommandLineArgs commandLineArgs;

    @Spy
    @InjectMocks
    Context context;

    FileSystem fileSystem;
    ResourceLoader resourceLoader;

    @Before
    public void setup() {
        context.registerBean(new EmptyStatistics());
        context.registerBean(new Gson());
        context.registerBean(Executors.newSingleThreadExecutor());

        fileSystem = new FileSystem(context);
        resourceLoader = new ResourceLoader(context, fileSystem);
    }

    @Test
    public void testLoad_SuccessfulImmediate() throws URISyntaxException, IOException {


        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("test.json").toURI());
        fileSystem.addPath(path.getParent());

        Resource resource = new Json(context);
        resource.setLocation(path);

        resourceLoader.load(resource, true);

        MatcherAssert.assertThat(resource.getState(), Is.is(Resource.AsyncState.SUCCESS));
    }


    @Test
    public void testLoad_FailureBadFileImmediate() throws URISyntaxException, IOException {


        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("bad.json").toURI());
        Resource resource = new Json(context);
        resource.setLocation(path);

        resourceLoader.load(resource, true);

        MatcherAssert.assertThat(resource.getState(), Is.is(Resource.AsyncState.FAILED));
    }

    @Test(expected = NullPointerException.class)
    public void testLoad_FailureNoPath() throws URISyntaxException, IOException {


        Resource resource = new Json(context);

        resourceLoader.load(resource, true);
    }

    @Test(expected = NullPointerException.class)
    public void testLoad_FailureNoResource() throws URISyntaxException, IOException {

        resourceLoader.load(null, true);
    }

    @Test(timeout = 5000L)
    public void testLoad_SuccessfulThreaded() throws URISyntaxException, IOException {

        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("test.json").toURI());
        Resource resource = new Json(context);
        resource.setLocation(path);

        resourceLoader.load(resource, false);

        while (resource.getState().equals(Resource.AsyncState.QUEUED) || resource.getState().equals(Resource.AsyncState.LOADING))
            ;

        MatcherAssert.assertThat(resource.getState(), Is.is(Resource.AsyncState.SUCCESS));
    }

    @Test(timeout = 5000L)
    public void testLoad_FailureThreaded() throws URISyntaxException, IOException {

        Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource("bad.json").toURI());
        Resource resource = new Json(context);
        resource.setLocation(path);

        resourceLoader.load(resource, false);

        while (resource.getState().equals(Resource.AsyncState.QUEUED) || resource.getState().equals(Resource.AsyncState.LOADING))
            ;

        MatcherAssert.assertThat(resource.getState(), Is.is(Resource.AsyncState.FAILED));
    }
}
