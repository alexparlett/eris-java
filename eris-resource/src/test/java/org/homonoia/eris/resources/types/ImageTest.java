package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.exceptions.ImageException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alexp on 25/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImageTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    ApplicationContext applicationContext;

    @InjectMocks
    Context context;

    @Test
    public void testLoad_Successful() throws IOException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("texture.jpg");

        Image image = new Image(context);
        image.load(resource.getInputStream());

        assertThat(image.getData(), is(not(nullValue())));
    }

    @Test(expected = NullPointerException.class)
    public void testLoad_NullStream() throws IOException {
        Image image = new Image(context);
        image.load(null);
    }

    @Test(expected = IOException.class)
    public void testLoad_EmptyFile() throws IOException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("bad.jpg");

        Image image = new Image(context);
        image.load(resource.getInputStream());
    }

    @Test
    public void testSave_Successful() throws IOException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("texture.jpg");

        Image image = new Image(context);
        image.load(resource.getInputStream());

        File file = folder.newFile("test.jpg");
        try(FileOutputStream outputStream = new FileOutputStream(file)) {
            image.save(outputStream);
        }

        assertThat(file.length(), greaterThan(0l));
    }

    @Test(expected = IOException.class)
    public void testSave_NullStream() throws IOException {
        Image image = new Image(context);

        File file = folder.newFile("test.jpg");
        try(FileOutputStream outputStream = new FileOutputStream(file)) {
            image.save(outputStream);
        }
    }

    @Test
    public void testResize_DownSuccessful() throws IOException, ImageException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("texture.jpg");

        Image image = new Image(context);
        image.load(resource.getInputStream());

        boolean success = image.resize(1024, 1024);

        assertThat(success, is(true));
        assertThat(image.getWidth(), is(1024));
        assertThat(image.getHeight(), is(1024));
    }

    @Test
    public void testResize_UpSuccessful() throws IOException, ImageException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("texture.jpg");

        Image image = new Image(context);
        image.load(resource.getInputStream());

        boolean success = image.resize(4096, 4096);

        assertThat(success, is(true));
        assertThat(image.getWidth(), is(4096));
        assertThat(image.getHeight(), is(4096));
    }

    @Test
    public void testResize_BadSize() throws IOException, ImageException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("texture.jpg");

        Image image = new Image(context);
        image.load(resource.getInputStream());

        boolean success = image.resize(0, 4096);

        assertThat(success, is(false));
        assertThat(image.getWidth(), is(2048));
        assertThat(image.getHeight(), is(2048));
    }

    @Test
    public void testResize_EqualSize() throws IOException, ImageException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("texture.jpg");

        Image image = new Image(context);
        image.load(resource.getInputStream());

        boolean success = image.resize(2048, 2048);

        assertThat(success, is(false));
        assertThat(image.getWidth(), is(2048));
        assertThat(image.getHeight(), is(2048));
    }


    @Test(expected = ImageException.class)
    public void testResize_NoDate() throws IOException, ImageException {
        Image image = new Image(context);
        image.resize(2048, 2048);
    }
}
