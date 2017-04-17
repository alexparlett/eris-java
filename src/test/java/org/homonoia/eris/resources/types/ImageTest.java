package org.homonoia.eris.resources.types;

import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.EmptyStatistics;
import org.homonoia.eris.resources.types.image.ImageException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 25/02/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class ImageTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

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
    public void testLoad_Successful() throws IOException {
        InputStream resource = ClassLoader.getSystemResourceAsStream("texture.jpg");

        Image image = new Image(context);
        image.load(resource);

        assertThat(image.getData(), is(not(nullValue())));
    }

    @Test(expected = NullPointerException.class)
    public void testLoad_NullStream() throws IOException {
        Image image = new Image(context);
        image.load(null);
    }

    @Test(expected = IOException.class)
    public void testLoad_EmptyFile() throws IOException {
        InputStream resource = ClassLoader.getSystemResourceAsStream("bad.jpg");

        Image image = new Image(context);
        image.load(resource);
    }

    @Test
    public void testSave_Successful() throws IOException {
        InputStream resource = ClassLoader.getSystemResourceAsStream("texture.jpg");

        File file = folder.newFile("test.png");

        Image image = new Image(context);
        image.setLocation(file.toPath());
        image.load(resource);
        image.save();

        assertThat(file.length(), Matchers.greaterThan(0L));
    }

    @Test(expected = IOException.class)
    public void testSave_NullStream() throws IOException {
        File file = folder.newFile("test.jpg");

        Image image = new Image(context);
        image.setLocation(file.toPath());
        image.save();
    }

    @Test
    public void testResize_DownSuccessful() throws IOException, ImageException {
        InputStream resource = ClassLoader.getSystemResourceAsStream("texture.jpg");

        Image image = new Image(context);
        image.load(resource);

        image.resize(1024, 1024);

        assertThat(image.getWidth(), is(1024));
        assertThat(image.getHeight(), is(1024));
    }

    @Test
    public void testResize_UpSuccessful() throws IOException, ImageException {
        InputStream resource = ClassLoader.getSystemResourceAsStream("texture.jpg");

        Image image = new Image(context);
        image.load(resource);

        image.resize(4096, 4096);

        assertThat(image.getWidth(), is(4096));
        assertThat(image.getHeight(), is(4096));
    }

    @Test(expected = ImageException.class)
    public void testResize_BadSize() throws IOException, ImageException {
        InputStream resource = ClassLoader.getSystemResourceAsStream("texture.jpg");

        Image image = new Image(context);
        image.load(resource);

        image.resize(0, 4096);
    }

    @Test
    public void testResize_EqualSize() throws IOException, ImageException {
        InputStream resource = ClassLoader.getSystemResourceAsStream("texture.jpg");

        Image image = new Image(context);
        image.load(resource);

        image.resize(2048, 2048);

        assertThat(image.getWidth(), is(2048));
        assertThat(image.getHeight(), is(2048));
    }


    @Test(expected = ImageException.class)
    public void testResize_NoDate() throws IOException, ImageException {
        Image image = new Image(context);
        image.resize(2048, 2048);
    }
}
